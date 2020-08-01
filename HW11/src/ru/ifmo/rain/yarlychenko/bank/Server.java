package ru.ifmo.rain.yarlychenko.bank;

import ru.ifmo.test.common.bank.Bank;
import ru.ifmo.test.common.bank.BankServer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * @author Nikolay Yarlychenko
 */
public class Server implements BankServer {


    private Bank bank;
    Registry registry;

    @Override
    public void start(int port) {
        try {
            bank = new RemoteBank(port);
            registry = LocateRegistry.createRegistry(port);
            registry.rebind("//localhost/bank", bank);
        } catch (RemoteException e) {
            System.err.println("Remote exception");
        }
        System.out.println("Server started");
    }

    @Override
    public void close() {
        try {
            registry.unbind("//localhost/bank");
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }
}
