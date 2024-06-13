package com.masai.service;


import com.google.rpc.context.AttributeContext;
import auth.Auth;
import auth.AuthServiceGrpc;
import com.masai.exception.CustomerNotFoundException;
import com.masai.exception.LoginException;
import com.masai.models.Customer;
import com.masai.models.UserSession;
import com.masai.repository.CustomerDao;
import com.masai.repository.SellerDao;
import com.masai.repository.SessionDao;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@GrpcService
public class AuthService extends AuthServiceGrpc.AuthServiceImplBase {

    @Autowired
    private SessionDao sessionDao;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private SellerDao sellerDao;

    @Override
    public void loginCustomer(Auth.LoginRequest request, StreamObserver<Auth.LoginResponse> responseObserver) {
        System.out.println("Received login request for email: " + request.getEmailId());
        String preToken = request.getPreToken();
        String emailId = request.getEmailId();
        String password = request.getPassword();

        String message = "";

        // Xử lý logic đăng nhập ở đây
        Optional<Customer> res = customerDao.findByEmailId(emailId);

        if(res.isEmpty())
            res("Customer record does not exist with given email", responseObserver);

        Customer existingCustomer = res.get();

        Optional<UserSession> opt = sessionDao.findByUserId(existingCustomer.getCustomerId());
        // check already log in?
        if(opt.isPresent()) {

            UserSession user = opt.get();

            if(user.getSessionEndTime().isBefore(LocalDateTime.now())) {
                sessionDao.delete(user);
            }
            else res("User already logged in", responseObserver);
        }


        if(existingCustomer.getPassword().equals(password)) {

            UserSession newSession = new UserSession();

            newSession.setUserId(existingCustomer.getCustomerId());
            newSession.setUserType("customer");
            newSession.setSessionStartTime(LocalDateTime.now());
            newSession.setSessionEndTime(LocalDateTime.now().plusHours(10));

            UUID uuid = UUID.randomUUID();
            String token = "customer_" + preToken;

            newSession.setToken(token);

            sessionDao.save(newSession);
            message = "Success";
        }
        else {
            res("Password Incorrect. Try again.", responseObserver);
            //throw new LoginException("Password Incorrect. Try again.");
        }
        // Ví dụ đơn giản trả về thông báo thành công

        Auth.LoginResponse response = Auth.LoginResponse.newBuilder()
                                              .setMessage(message)
                                              .build();

        // Gửi phản hồi về cho client
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    public void getBook() {
        System.out.println("test");
    }

    public void res (String message, StreamObserver<Auth.LoginResponse> responseObserver) {
        Auth.LoginResponse response = Auth.LoginResponse.newBuilder()
                .setMessage(message)
                .build();

        // Gửi phản hồi về cho client
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
