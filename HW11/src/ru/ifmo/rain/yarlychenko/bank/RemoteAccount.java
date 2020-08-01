package ru.ifmo.rain.yarlychenko.bank;

import ru.ifmo.test.common.bank.Account;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * @author Nikolay Yarlychenko
 */
public class RemoteAccount extends UnicastRemoteObject implements Account {
    private final String id;
    private int amount;


    public RemoteAccount(String id) throws RemoteException {
        super();
        this.id = id;
        this.amount = 0;
    }

    @Override
    public String getId() throws RemoteException {
        return id;
    }

    @Override
    public int getAmount() throws RemoteException {

        return amount;
    }

    @Override
    public void setAmount(int newAmount) throws RemoteException {
        amount = newAmount;
    }
}
