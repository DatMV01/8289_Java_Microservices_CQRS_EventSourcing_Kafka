package com.techbank.account.query.api.queries;

import com.techbank.cqrs.queries.BaseQuery;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FindAccountByHolderQuery extends BaseQuery {
    private String accountHolder;
}
