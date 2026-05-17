package com.techbank.account.cmd.commands.account;

import com.techbank.cqrs.commands.BaseCommand;

public class CloseAccountCommand extends BaseCommand {
    public CloseAccountCommand(String accountId) {
        super(accountId);
    }
}
