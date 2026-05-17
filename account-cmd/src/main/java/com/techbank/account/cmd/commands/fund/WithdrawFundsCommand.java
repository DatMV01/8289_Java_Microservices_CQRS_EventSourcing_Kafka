package com.techbank.account.cmd.commands.fund;

import com.techbank.cqrs.commands.BaseCommand;
import lombok.Data;

@Data
public class WithdrawFundsCommand extends BaseCommand {
    private double amount;
}
