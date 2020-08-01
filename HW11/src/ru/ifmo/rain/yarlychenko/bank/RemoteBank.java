package ru.ifmo.rain.yarlychenko.bank;

import ru.ifmo.test.common.bank.Account;
import ru.ifmo.test.common.bank.Bank;
import ru.ifmo.test.common.bank.Person;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author Nikolay Yarlychenko
 */
public class RemoteBank extends UnicastRemoteObject implements Bank {

    private final ConcurrentMap<String, Account> accounts = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Person> persons = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Set<String>> personAccountsNames = new ConcurrentHashMap<>();

    public RemoteBank(int port) throws RemoteException {
        super(port);
    }

    @Override
    public Account createAccount(String id) throws RemoteException {
        final Account account = new RemoteAccount(id);
        if (accounts.putIfAbsent(id, account) == null) {
            return account;
        } else {
            return getAccount(id);
        }
    }

    @Override
    public Account getAccount(String id) throws RemoteException {
        return accounts.get(id);
    }

    @Override
    public Person createPersonAccount(String accountName, Person remotePerson) throws RemoteException {

        if (remotePerson == null) {
            return null;
        }

        String id = remotePerson.getPassport() + ":" + accountName;

        Account account = accounts.get(id);
        if (account != null) {
            return remotePerson;
        }
        if (remotePerson instanceof LocalPerson) {
            ((LocalPerson) remotePerson).addAccount(id, new LocalAccount(id));
        } else {
            account = new RemoteAccount(id);
            accounts.put(id, account);
            personAccountsNames.get(remotePerson.getPassport()).add(id);
        }
        return remotePerson;
    }

    @Override
    public Person createPerson(String name, String surname, String passport) throws RemoteException {
        Person person = persons.get(passport);
        if (person != null) {
            return person;
        }
        person = new RemotePerson(name, surname, passport, this);
        persons.put(passport, person);
        personAccountsNames.put(passport, new ConcurrentSkipListSet<>());
        return person;
    }

    @Override
    public Person getLocalPerson(String passport) throws RemoteException {
        Person person = persons.get(passport);
        if (person == null) {
            return null;
        }

        Set<String> accounts = getPersonAccounts(persons.get(passport));
        ConcurrentHashMap<String, LocalAccount> personAccounts = new ConcurrentHashMap<>();

        for (String name : accounts) {
            Account account = getAccount(name);
            personAccounts.put(name, new LocalAccount(account.getId(), account.getAmount()));
        }
        return new LocalPerson(person.getName(), person.getSurname(), person.getPassport(), personAccounts);
    }

    private Set<String> getPersonAccounts(Person person) throws RemoteException {
        return personAccountsNames.get(person.getPassport());
    }


    @Override
    public Person getRemotePerson(String passport) throws RemoteException {
        return persons.get(passport);
    }
}
