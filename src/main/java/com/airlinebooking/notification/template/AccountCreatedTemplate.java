package com.airlinebooking.notification.template;

public class AccountCreatedTemplate {
    public static String build(String fullName, String passTemp){
        return
                "Xin chào: \n" + fullName + "\n"
                        + "Mật khẩu tạm thời tài khoản của bạn là:\n" + passTemp + "\n"
                        + "Mật khẩu này có hiệu lực trong 15 phút, Vui lòng đổi mật khẩu sau lần đăng nhập đầu tiên \n"
                        +"Trân trọng.";

    }
}