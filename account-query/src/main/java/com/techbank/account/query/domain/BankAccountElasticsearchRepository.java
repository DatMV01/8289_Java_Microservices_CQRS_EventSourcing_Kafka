package com.techbank.account.query.domain;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankAccountElasticsearchRepository extends ElasticsearchRepository<BankAccountDocument, String> {
}
