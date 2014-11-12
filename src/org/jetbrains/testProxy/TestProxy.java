package org.jetbrains.testProxy;

import io.netty.handler.codec.http.*;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

public final class TestProxy {
  public static void main(String[] args) throws Exception {
    HttpProxyServer server =
      DefaultHttpProxyServer.bootstrap()
        .withPort(8060)
        .withFiltersSource(new HttpFiltersSourceAdapter() {
          @Override
          public HttpFilters filterRequest(HttpRequest originalRequest) {
            return new HttpFiltersAdapter(originalRequest) {
              @Override
              public HttpObject proxyToClientResponse(HttpObject httpObject) {
                if (httpObject instanceof HttpResponse) {
                  if (!originalRequest.headers().contains(HttpHeaders.Names.PROXY_AUTHORIZATION)) {
                    DefaultFullHttpResponse response =
                      new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.PROXY_AUTHENTICATION_REQUIRED);
//                    response.headers().set("Proxy-Authenticate", "Negotiate");
                    response.headers().set("Proxy-Authenticate", "Kerberos");
//                    response.headers().set("Proxy-Authenticate", "NTLM");
                    return response;
                  }
                }
                return httpObject;
              }
            };
          }
        })
        .start();
  }
}