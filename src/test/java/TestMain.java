import com.wangjunneil.schedule.utility.DateTimeUtil;

import java.util.Date;

/**
 * Created by wangjun on 8/4/16.
 */
public class TestMain {
    public static void main(String[] args) {
        long time = Long.parseLong("1470280866001");
        int expireIn = 86400;
        Date expireDate = DateTimeUtil.getExpireDate(time, expireIn);


        System.out.println(DateTimeUtil.dateFormat(expireDate, "yyyy-MM-dd HH:mm:ss"));
//        Date date = new Date(l);
//        System.out.println(DateTimeUtil.dateFormat(date, "yyyy-MM-dd HH:mm:ss"));
//
//        System.out.println(DateTimeUtil.dateFormat(DateTimeUtil.getExpireDate(l, 86400), "yyyy-MM-dd HH:mm:ss"));
    }
}
