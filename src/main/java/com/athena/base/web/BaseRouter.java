package com.athena.base.web;

public class BaseRouter<BaseServerInstance extends BaseServer> {
    protected BaseServerInstance serverInstance;

    public BaseRouter(BaseServerInstance serverInstance) {
        this.serverInstance = serverInstance;
    }
}
