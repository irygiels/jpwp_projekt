package cisco.demo;


import org.json.JSONObject;


public interface Caller {
    void handleResponse(JSONObject result);
    String getAddress();
    JSONObject getData();
    int getDataLength();
}