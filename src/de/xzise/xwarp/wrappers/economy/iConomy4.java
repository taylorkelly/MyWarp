package de.xzise.xwarp.wrappers.economy;

import com.nijiko.coelho.iConomy.iConomy;
import com.nijiko.coelho.iConomy.system.Account;
import com.nijiko.coelho.iConomy.system.Bank;

public class iConomy4 implements EconomyWrapper {

    private final Bank bank;
    
    public final class Account4 implements AccountWrapper {

        private final Account account;
        
        public Account4(Account account) {
            this.account = account;
        }
        
        @Override
        public boolean hasEnough(double price) {
            return this.account.hasEnough(price);
        }

        @Override
        public void add(double price) {
            this.account.add(price);
        }
        
    }
    
    public iConomy4() {
        this.bank = iConomy.getBank();
    }
    
    @Override
    public AccountWrapper getAccount(String name) {
        return new Account4(this.bank.getAccount(name));
    }

    @Override
    public String format(double price) {
        return this.bank.format(price);
    }

}
