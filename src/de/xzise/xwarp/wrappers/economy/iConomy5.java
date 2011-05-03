package de.xzise.xwarp.wrappers.economy;

import com.iConomy.iConomy;
import com.iConomy.system.Account;

public class iConomy5 implements EconomyWrapper {
    
    public final class Account5 implements AccountWrapper {

        private Account account;
        
        public Account5(Account account) {
            this.account = account;
        }
        
        @Override
        public boolean hasEnough(double price) {
            return this.account.getHoldings().hasEnough(price);
        }

        @Override
        public void add(double price) {
            this.account.getHoldings().add(price);
        }
        
    }
    
    @Override
    public AccountWrapper getAccount(String name) {
        return new Account5(iConomy.getAccount(name));
    }

    @Override
    public String format(double price) {
        return iConomy.format(price);
    }

}
