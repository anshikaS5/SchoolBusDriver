package com.vmoksha.schoolbusdriver.route;

import java.util.ArrayList;

public interface RoutingListener {
    void onRoutingFailure();

    void onRoutingStart();

    void onRoutingSuccess(ArrayList<com.vmoksha.schoolbusdriver.route.Route> route, int shortestRouteIndex);

    void onRoutingCancelled();
}
