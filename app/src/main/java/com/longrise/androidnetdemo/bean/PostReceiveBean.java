package com.longrise.androidnetdemo.bean;

public class PostReceiveBean {

    /**
     * success : true
     * code : 10000
     * message : 上传成功3个文件，路径：E:/codes/Idear/SobNetworkCourseServer/target/classes/sobUpload
     * data : null
     */

    private boolean success;
    private int code;
    private String message;
    private Object data;

    public PostReceiveBean(boolean success, int code, String message, Object data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "PostReceiveBean{" +
                "success=" + success +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
