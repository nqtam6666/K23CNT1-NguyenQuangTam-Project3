package com.Ngay02.nqt_Ngay02.ioc;

class Service {
    public void serve() {
        System.out.println("Service is serving");
    }
}
class Client {
    private Service service;
    public Client() {
// Client tự tạo đối tượng Service
        service = new Service();
    }
    public void doSomething() {
        service.serve();
    }
}
public class NonIOC {
    public static void main(String[] args) {
        Client client = new Client();
        client.doSomething();
    }
}