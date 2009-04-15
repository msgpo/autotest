package autotest.tko;

import autotest.common.JsonRpcCallback;
import autotest.common.JsonRpcProxy;
import autotest.common.Utils;
import autotest.tko.TableView.TableSwitchListener;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

abstract class Plot extends Composite {
    protected final static JsonRpcProxy rpcProxy = JsonRpcProxy.getProxy();

    private String rpcName;
    private HTML plotElement = new HTML();
    protected TableSwitchListener listener;

    @SuppressWarnings("unused") // used from native code (setDrilldownTrigger)
    private String callbackName;

    private static class DummyRpcCallback extends JsonRpcCallback {
        @Override
        public void onSuccess(JSONValue result) {}
    }

    public Plot(String rpcName, String callbackName) {
        this.rpcName = rpcName;
        this.callbackName = callbackName;
        initWidget(plotElement);
    }

    /**
     * This function is called at initialization time and allows the plot to put native 
     * callbacks in place for drilldown functionality from graphs.
     */
    public native void setDrilldownTrigger() /*-{
        var instance = this;
        var name = this.@autotest.tko.Plot::callbackName;
        $wnd[name] = function(drilldownParams) {
            instance.@autotest.tko.Plot::showDrilldown(Lcom/google/gwt/core/client/JavaScriptObject;)(drilldownParams);
        }
    }-*/;

    @SuppressWarnings("unused") // called from native code (see setDrilldownTrigger)
    private void showDrilldown(JavaScriptObject drilldownParamsJso) {
        UncaughtExceptionHandler handler = GWT.getUncaughtExceptionHandler();
        if (handler == null) {
            showDrilldownImpl(new JSONObject(drilldownParamsJso));
        }

        try {
            showDrilldownImpl(new JSONObject(drilldownParamsJso));
        } catch (Throwable throwable) {
            handler.onUncaughtException(throwable);
        }
    }

    protected abstract void showDrilldownImpl(JSONObject drilldownParams);

    public void refresh(JSONObject params, final JsonRpcCallback callback) {
        rpcProxy.rpcCall(rpcName, params, new JsonRpcCallback() {
            @Override
            public void onSuccess(JSONValue result) {
                plotElement.setHTML(Utils.jsonToString(result));
                callback.onSuccess(result);
            }

            @Override
            public void onError(JSONObject errorObject) {
                callback.onError(errorObject);
            }
        });
    }

    public void refresh(JSONObject params) {
        refresh(params, new DummyRpcCallback());
    }

    public void setListener(TableSwitchListener listener) {
        this.listener = listener;
    }
}
