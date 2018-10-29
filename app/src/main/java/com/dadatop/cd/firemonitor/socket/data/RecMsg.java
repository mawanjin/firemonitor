package com.dadatop.cd.firemonitor.socket.data;

import com.xuhao.android.common.interfacies.client.msg.ISendable;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public class RecMsg implements ISendable {
    private String str = "";

    public RecMsg(String msg) {

        str = "{\"no\":99,\"msg\": \""+msg+"\"}".trim();

//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("data", "{\"no\":99,msg:\""+msg+"\"}");
//            str = jsonObject.toString();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public byte[] parse() {
        //Build the byte array according to the server's parsing rules
        byte[] body = str.getBytes(Charset.defaultCharset());
        ByteBuffer bb = ByteBuffer.allocate(4 + body.length);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.putInt(body.length);
        bb.put(body);
        return bb.array();

    }
}
