package com.techbank.account.cmd.commands.account;


import com.techbank.account.common.dto.AccountType;
import com.techbank.cqrs.commands.BaseCommand;
import lombok.Data;

@Data
public class OpenAccountCommand extends BaseCommand {
    private String accountHolder;
    private AccountType accountType;
    private double openingBalance;
}