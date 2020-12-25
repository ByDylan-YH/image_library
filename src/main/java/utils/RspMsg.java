package utils;

public class RspMsg {
    private int rspCode;
    private String rspMsg;
    private long totalCount;
    private Object rspBin;

    public RspMsg() {
        this.rspCode = 0;
        this.rspMsg = "成功";
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public int getRspCode() {
        return rspCode;
    }

    public void setRspCode(int rspCode) {
        this.rspCode = rspCode;
    }

    public String getRspMsg() {
        return rspMsg;
    }

    public void setRspMsg(String rspMsg) {
        this.rspMsg = rspMsg;
    }

    public Object getRspBin() {
        return rspBin;
    }

    public void setRspBin(Object rspBin) {
        this.rspBin = rspBin;
    }
}
