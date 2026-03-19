package com.skillchain.service.state;

import com.skillchain.enums.OrderStatus;

public class OrderStateFactory {

    public static OrderState getState(OrderStatus status) {
        switch (status) {
            case PENDING_PAYMENT:
                return new PendingPaymentState();
            case PENDING_ACCEPT:
                return new PendingAcceptState();
            case ACCEPTED:
                return new AcceptedState();
            case IN_SERVICE:
                return new InServiceState();
            case PENDING_REVIEW:
                return new PendingReviewState();
            case COMPLETED:
            case REFUNDING:
            case REFUNDED:
            case CANCELLED:
            default:
                throw new RuntimeException("Invalid order state: " + status);
        }
    }
}