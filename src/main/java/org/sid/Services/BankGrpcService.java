package org.sid.Services;

import io.grpc.stub.StreamObserver;
import org.sid.stubs.Bank;
import org.sid.stubs.BankServiceGrpc;

import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.random;

public class BankGrpcService extends BankServiceGrpc.BankServiceImplBase {


    private static final Map<String,Double> currencies = Map.of(
            "USD", 1.0,
            "EUR", 0.85,
            "GBP", 0.78,
            "JPY", 110.0
    );

    @Override
    public void convert(Bank.ConvertCurrencyRequest request, StreamObserver<Bank.ConvertCurrencyResponse> responseObserver) {

        String from = request.getCurrencyFrom();
        String to = request.getCurrencyTo();
        double amount = request.getAmount();

        double result = amount * currencies.get(from) / currencies.get(to);

        Bank.ConvertCurrencyResponse response = Bank.ConvertCurrencyResponse.newBuilder()
                .setCurrencyFrom(from)
                .setCurrencyTo(to)
                .setAmount(amount)
                .setResult(result)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

    @Override
    public void getStream(Bank.ConvertCurrencyRequest request, StreamObserver<Bank.ConvertCurrencyResponse> responseObserver) {
        String from = request.getCurrencyFrom();
        String to = request.getCurrencyTo();
        double amount = request.getAmount();

        Timer timer = new Timer();
        timer.schedule( new TimerTask() {
            int counter=0;
            @Override
            public void run() {
                Bank.ConvertCurrencyResponse response = Bank.ConvertCurrencyResponse.newBuilder()
                        .setCurrencyFrom(from)
                        .setCurrencyTo(to)
                        .setAmount(amount)
                        .setResult(amount * currencies.get(from) / currencies.get(to))
                        .build();
                responseObserver.onNext(response);
                ++counter;
                if(counter==20){
                    responseObserver.onCompleted();
                    timer.cancel();
                }
            }
        }, 1000, 1000);

    }

    @Override
    public StreamObserver<Bank.ConvertCurrencyRequest> performStream(StreamObserver<Bank.ConvertCurrencyResponse> responseObserver) {
        return new StreamObserver<Bank.ConvertCurrencyRequest>() {

            double sum=0;
            double result=0;

            @Override
            public void onNext(Bank.ConvertCurrencyRequest convertCurrencyRequest) {
                String from = convertCurrencyRequest.getCurrencyFrom();
                String to = convertCurrencyRequest.getCurrencyTo();
                double amount = convertCurrencyRequest.getAmount();
                double fromRate = currencies.get(from);
                double toRate = currencies.get(to);
                sum+= convertCurrencyRequest.getAmount();
                result = sum * fromRate / toRate;

            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                Bank.ConvertCurrencyResponse response = Bank.ConvertCurrencyResponse.newBuilder()
                        .setResult(result)
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<Bank.ConvertCurrencyRequest> fullCurrencyStream(StreamObserver<Bank.ConvertCurrencyResponse> responseObserver) {
        return new StreamObserver<Bank.ConvertCurrencyRequest>() {
            @Override
            public void onNext(Bank.ConvertCurrencyRequest convertCurrencyRequest) {
                Bank.ConvertCurrencyResponse response = Bank.ConvertCurrencyResponse.newBuilder()
                        .setCurrencyFrom(convertCurrencyRequest.getCurrencyFrom())
                        .setCurrencyTo(convertCurrencyRequest.getCurrencyTo())
                        .setAmount(convertCurrencyRequest.getAmount())
                        .setResult(convertCurrencyRequest.getAmount() * random() *88)
                        .build();
                responseObserver.onNext(response);
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
