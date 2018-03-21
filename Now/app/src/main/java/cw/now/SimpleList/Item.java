package cw.now.SimpleList;

/**
 * Created by Thinkpad on 2018/2/9.
 */

public class Item {
    private String name;
    private String detail;
    private int Key;
   // private boolean obs_achv=false;

    public Item(String name, String detail,int key) {
        this.name = name;  //接收的参数值传入方法的变量值
        this.detail = detail;
        this.Key=key;
    }


    public String getName() {
        return name;
    }

    public int getKey(){return Key;}

    public String getDetail() {
        return detail;
    }

}