package ru.ifmo.rain.yarlychenko.bank;

import ru.ifmo.test.common.bank.Account;
import ru.ifmo.test.common.bank.Person;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Nikolay Yarlychenko
 */

public class LocalPerson implements Person, Serializable {
    private final String name;
    private final String surname;
    private final String passportID;
    ConcurrentMap<String, LocalAccount> accounts;

    public LocalPerson(String name, String surname, String passportID, ConcurrentMap<String, LocalAccount> accounts) {
        this.name = name;
        this.surname = surname;
        this.passportID = passportID;
        this.accounts = accounts;
    }

    @Override
    public String getName() throws RemoteException {
        return name;
    }

    @Override
    public String getSurname() throws RemoteException {
        return surname;
    }

    @Override
    public String getPassport() throws RemoteException {
        return passportID;
    }

    @Override
    public Account getAccount(String accountName) throws RemoteException {
        return accounts.get(passportID + ":" + accountName);
    }


    void addAccount(String id, LocalAccount localAccount) {
        accounts.put(id, localAccount);
    }

}
