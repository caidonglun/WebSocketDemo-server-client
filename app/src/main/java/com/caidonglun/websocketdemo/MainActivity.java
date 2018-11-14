package com.caidonglun.websocketdemo;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.ws.WebSocket;
import okhttp3.ws.WebSocketCall;
import okhttp3.ws.WebSocketListener;
import okio.Buffer;

public class MainActivity extends AppCompatActivity {

    Button btn_start_socket,btn_socketClient,btn_close_clear,btn_sendMessage;
    EditText et_roomName,et_editMessage;
    TextView tv_show;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x111:
                Toast.makeText(MainActivity.this, "打开了", Toast.LENGTH_SHORT).show();
                break;
                case 0x112:
                    tv_show.append(msg.obj.toString()+"\n");
                    Toast.makeText(MainActivity.this, "内容为："+msg.obj, Toast.LENGTH_SHORT).show();

            }


        }
    };


    final  static OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
            .readTimeout(3000, TimeUnit.SECONDS)//设置读取超时时间
            .writeTimeout(3000, TimeUnit.SECONDS)//设置写的超时时间
            .connectTimeout(3000, TimeUnit.SECONDS)//设置连接超时时间
            .build();

    public WebSocket mainwebSocket=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);



        btn_start_socket= (Button) findViewById(R.id.btn_start_socket);
        btn_socketClient= (Button) findViewById(R.id.btn_socketClient);
        tv_show= (TextView) findViewById(R.id.tv_show);
        btn_close_clear= (Button) findViewById(R.id.btn_close_clear);
        et_editMessage= (EditText) findViewById(R.id.et_editMessage);
        btn_sendMessage= (Button) findViewById(R.id.btn_sendMessage);
        btn_sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mainwebSocket.sendMessage(RequestBody.create(WebSocket.TEXT,""+et_editMessage.getText().toString()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        btn_close_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_show.setText("");
                try {
                    mainwebSocket.close(1000,"这是上面只");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        et_roomName= (EditText) findViewById(R.id.et_roomName);
        btn_socketClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url="ws://10.0.2.2:8080/websocket/"+et_roomName.getText().toString();
                Toast.makeText(MainActivity.this, ""+url, Toast.LENGTH_SHORT).show();
                Request request = new Request.Builder().url(url).build();
                final WebSocketCall webSocketCall = WebSocketCall.create(mOkHttpClient, request);
                webSocketCall.enqueue(new WebSocketListener() {

                                          @Override
                                          public void onOpen(WebSocket webSocket, Response response) {
                                              mainwebSocket=webSocket;
                                              Message message=new Message();
                                              message.what=0x111;
                                              handler.sendMessage(message);
                                          }

                                          @Override
                                          public void onFailure(IOException e, Response response) {
                                              System.out.print("失败了————————————————————————————————————————");
                                          }

                                          @Override
                                          public void onMessage(ResponseBody message) throws IOException {
                                              Message message1=new Message();
                                              message1.what=0x112;
                                              message1.obj=message.string();
                                              handler.sendMessage(message1);

                                          }

                                          @Override
                                          public void onPong(Buffer payload) {

                                          }

                                          @Override
                                          public void onClose(int code, String reason) {
//                                              sendExecutor.shutdown();

                                          }
                                      });

//下面内容算是模板
//                 webSocketCall.enqueue(new WebSocketListener() {
//                    private final ExecutorService sendExecutor = Executors.newSingleThreadExecutor();
//                    private WebSocket webSocket;
//                    @Override
//                    public void onOpen(final WebSocket webSocket, final Response response) {
//                        Log.d("WebSocketCall", "websocket开启中啊 ））））））））））））））））））");
//                        Toast.makeText(MainActivity.this, "开启 zhongdsafasdfas", Toast.LENGTH_SHORT).show();
//                        this.webSocket=webSocket;
//                        sendExecutor.execute(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    Thread.sleep(1000);
//                                    webSocket.sendMessage(RequestBody.create(WebSocket.TEXT, "你好"));//发送消息
//                                } catch (IOException e) {
//                                    e.printStackTrace(System.out);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
//                    }
//                    /**
//                     * 连接失败
//                     * @param e
//                     * @param response Present when the failure is a direct result of the response (e.g., failed
//                     * upgrade, non-101 response code, etc.). {@code null} otherwise.
//                     */
//                    @Override
//                    public void onFailure(IOException e, Response response) {
//                        Log.d("WebSocketCall","连接失败！！！");
//                    }
//
//                    /**
//                     * 接收到消息
//                     * @param message
//                     * @throws IOException
//                     */
//                    @Override
//                    public void onMessage(ResponseBody message) throws IOException {
//                        final RequestBody response;
//                        Log.d("WebSocketCall", "onMessage:" + message.source().readByteString().utf8());
//                        if (message.contentType() == WebSocket.TEXT) {//
//                            response = RequestBody.create(WebSocket.TEXT, "你好");//文本格式发送消息
//                        } else {
//                            BufferedSource source = message.source();
//                            Log.d("WebSocketCall", "onMessage:" + source.readByteString());
//                            response = RequestBody.create(WebSocket.BINARY, source.readByteString());
//                        }
//                        message.source().close();
//                        sendExecutor.execute(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    Thread.sleep(1000);
//                                    webSocket.sendMessage(response);//发送消息
//                                } catch (IOException e) {
//                                    e.printStackTrace(System.out);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onPong(Buffer payload) {
//                        Log.d("WebSocketCall", "onPong:");
//                    }
//
//
//                    /**
//                     * 关闭
//                     * @param code The <a href="http://tools.ietf.org/html/rfc6455#section-7.4.1">RFC-compliant</a>
//                     * status code.
//                     * @param reason Reason for close or an empty string.
//                     */
//                    @Override
//                    public void onClose(int code, String reason) {
//                        sendExecutor.shutdown();
//                    }
//            });

            }
        });


        btn_start_socket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}
