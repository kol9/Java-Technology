package ru.ifmo.rain.yarlychenko.bank;

import ru.ifmo.test.common.bank.Account;
import ru.ifmo.test.common.bank.Bank;
import ru.ifmo.test.common.bank.BankClient;
import ru.ifmo.test.common.bank.Person;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * @author Nikolay Yarlychenko
 */
public class Client implements BankClient {

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

    }

    @Override
    public int change(String name, String surname, String passport, String accountName, String modification) throws RemoteException {
        Person person = bank.createPerson(name, surname, passport);

        if (person.getName().equals(name) && person.getSurname().equals(surname) && person.getPassport().equals(passport)) {
            bank.createPersonAccount(accountName, person);
            Account account = bank.getAccount(passport + ":" + accountName);
            account.setAmount(account.getAmount() + Integer.parseInt(modification));
            return account.getAmount();
        }
        return 0;
    }

    @Override
    public void close() {
    }
}
