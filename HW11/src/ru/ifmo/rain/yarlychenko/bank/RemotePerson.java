package ru.ifmo.rain.yarlychenko.bank;

import ru.ifmo.test.common.bank.Account;
import ru.ifmo.test.common.bank.Person;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Nikolay Yarlychenko
 */
public class RemotePerson extends UnicastRemoteObject implements Person {
    private final String name;
    private final String surname;
    private final String passportID;
    private final RemoteBank bank;

    public RemotePerson(String name, String surname, String passportID, RemoteBank remoteBank) throws RemoteException {
        super();
        this.name = name;
        this.surname = surname;
        this.passportID = passportID;
        this.bank = remoteBank;
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
        return bank.getAccount(passportID + ":" + accountName);
    }

}
