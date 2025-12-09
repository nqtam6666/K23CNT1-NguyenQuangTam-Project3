package com.Ngay02.nqt_Ngay02.ioc;
class IoCService {
    public void serve() {
        System.out.println("Service is serving");
    }
}
class IoCClient {
    private IoCService iocService;
    // Dùng DI để truyền vào service thay vì tự tạo nó
    public IoCClient(IoCService service) {
        this.iocService = service;
    }
    public void doSomething() {
        iocService.serve();
    }
}
public class IOC {
    public static void main(String[] args) {
// Tạo đối tượng Service và truyền nó vào Client
        IoCService service = new IoCService ();
        IoCClient client = new IoCClient(service);
        client.doSomething();
    }
}