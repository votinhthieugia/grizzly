package com.athena.base.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RouteMatcher {
    private static String escapedDelimiter = escapeString("/");
    private static Pattern pathRegexp = null;

    private static class Token {
        public String path;
        public String name;
        public String prefix;
        public boolean optional;
        public boolean repeat;
        public boolean partial;
        public String pattern;

        public Token(String path) {
            this.path = path;
        }

        public Token(String name, String prefix, boolean optional, boolean repeat, boolean partial, String pattern) {
            this.path = null;
            this.name = name;
            this.prefix = prefix;
            this.optional = optional;
            this.repeat = repeat;
            this.partial = partial;
            this.pattern = pattern;
        }
    }

    private static String escapeString(String str) {
        return str.replaceAll("([.+*?=^!:${}()\\[\\]|\\/\\\\])", "\\\\$1");
    }

    private static String escapeGroup(String group) {
        return group.replaceAll("([=!:$\\/()])", "\\\\$1");
    }

    private static List<Token> parse(String str) {
        List<Token> tokens = new ArrayList<Token>();
        int key = 0;
        int index = 0;
        String path = "";
        String defaultDelimiter = "/";

        Matcher res = pathRegexp.matcher(str);
        while (res.find()) {
            String m = res.group(0);
            String escaped = res.groupCount() > 1 ? res.group(1) : null;
            int offset = res.start();
            path += str.substring(index, offset);
            index = offset + m.length();

            if (escaped != null) {
                path += escaped.substring(1, 1);
                continue;
            }

            String next = index < str.length() ? str.substring(index, index) : null;
            String prefix = res.groupCount() > 2 ? res.group(2) : null;
            String name = res.groupCount() > 3 ? res.group(3) : null;
            String capture = res.groupCount() > 4 ? res.group(4) : null;
            String group = res.groupCount() > 5 ? res.group(5) : null;
            String modifier = res.groupCount() > 6 ? res.group(6) : null;
            String asterisk = res.groupCount() > 7 ? res.group(7) : null;

            if (path.length() > 0) {
                tokens.add(new Token(path));
                path = "";
            }

            boolean partial = prefix != null && next != null && !next.equals(prefix);
            boolean repeat = modifier != null && (modifier.equals("+") || modifier.equals("*"));
            boolean optional = modifier != null && (modifier.equals("?") || modifier.equals("*"));
            String delimiter = prefix != null ? prefix : defaultDelimiter;
            String pattern = capture != null ? capture : group;

            tokens.add(new Token(
                    name != null ? name : String.valueOf(key++),
                    prefix != null ? prefix : "",
                    optional,
                    repeat,
                    partial,
                    pattern != null ? escapeGroup(pattern) : (asterisk != null ? ".*" : "[^" + escapeString(delimiter) + "]+?")
            ));
        }

        if (index < str.length()) {
            path += str.substring(index);
        }

        if (path.length() > 0) {
            tokens.add(new Token(path));
        }

        return tokens;
    }

    private Pattern regexp;
    private List<String> keys;
    private Matcher matcher;

    public RouteMatcher(String path) {
        if (pathRegexp == null) {
            pathRegexp = Pattern.compile("(\\\\.)|([\\/.])?(?:(?:\\:(\\w+)(?:\\(((?:\\\\.|[^\\\\()])+)\\))?|\\(((?:\\\\.|[^\\\\()])+)\\))([+*?])?|(\\*))");
        }

        keys = new ArrayList<String>();

        List<Token> tokens = parse(path);
        String route = "";

        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);

            if (token.path != null) {
                route += escapeString(token.path);
            } else {
                String prefix = escapeString(token.prefix);
                String capture = "(?:" + token.pattern + ")";

                keys.add(token.name);

                if (token.repeat) {
                    capture += "(?:" + prefix + capture + ")*";
                }

                if (token.optional) {
                    if (!token.partial) {
                        capture = "(?:" + prefix + "(" + capture + "))?";
                    } else {
                        capture = prefix + "(" + capture + ")?";
                    }
                } else {
                    capture = prefix + "(" + capture + ")";
                }

                route += capture;
            }
        }

        if (route.endsWith(escapedDelimiter)) {
            route = route.substring(0, route.length() - escapedDelimiter.length() + 1);
        }
        route += "(?:" + escapedDelimiter + "(?=$))?$";

        this.regexp = Pattern.compile(route);
    }

    public boolean matches(String route) {
        matcher = regexp.matcher(route);
        if (!matcher.find()) {
            matcher = null;
        }
        return matcher != null;
    }

    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();
        if (matcher != null) {
            for (int i = 0; i < keys.size(); i++) {
                try {
                    params.put(keys.get(i), matcher.group(i + 1));
                } catch (Exception e) {
                    // ignore
                }
            }
        }
        return params;
    }
}
