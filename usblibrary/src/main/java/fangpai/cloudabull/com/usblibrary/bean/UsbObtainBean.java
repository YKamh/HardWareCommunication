package fangpai.cloudabull.com.usblibrary.bean;

import java.util.List;

/**
 * Created by Administrator on 2018/1/6.
 */

public class UsbObtainBean {

    private List<UsbDataBean> escList;//小票

    private List<UsbDataBean> tscList;//标签

    public List<UsbDataBean> getEscList() {
        return escList;
    }

    public void setEscList(List<UsbDataBean> escList) {
        this.escList = escList;
    }

    public List<UsbDataBean> getTscList() {
        return tscList;
    }

    public void setTscList(List<UsbDataBean> tscList) {
        this.tscList = tscList;
    }

    public static class UsbDataBean{
        private int vid;
        private int pid;

        public UsbDataBean() {
        }

        public UsbDataBean(int vid, int pid) {
            this.vid = vid;
            this.pid = pid;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            UsbDataBean that = (UsbDataBean) o;

            if (vid != that.vid) return false;
            return pid == that.pid;
        }

        @Override
        public int hashCode() {
            int result = vid;
            result = 31 * result + pid;
            return result;
        }

        public int getVid() {
            return vid;
        }

        public void setVid(int vid) {
            this.vid = vid;
        }

        public int getPid() {
            return pid;
        }

        public void setPid(int pid) {
            this.pid = pid;
        }
    }

}
