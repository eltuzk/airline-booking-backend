package com.airlinebooking.notification.template;

public class PaymentSuccessTemplate {
    public static String build(boolean success, String transactionId) {
        return success
                ? "Giao dịch " + transactionId + " đã được xử lý thành công."
                : "Giao dịch " + transactionId + " thất bại. Vui lòng thử lại.";
    }
}
