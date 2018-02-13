package com.athena.base.web;

import java.lang.reflect.Method;

public class RouteHandler {
    protected BaseRouter router;
    protected Method method;
    protected boolean async;
    protected RouteMatcher matcher;
    protected Class<?> requestModel;
    protected Class<?> responseModel;
}
