package ru.ifmo.rain.yarlychenko.bank;

import ru.ifmo.test.common.bank.Account;

import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * @author Nikolay Yarlychenko
 */

public class LocalAccount implements Account, Serializable {
    private final String id;
    private int amount;

    public LocalAccount(String id) {
        this.id = id;
        this.amount = 0;
    }

    public LocalAccount(String id, final int amount) {
        this.id = id;
        this.amount = amount;
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
