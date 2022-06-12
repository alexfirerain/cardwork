package ru.netology.cardwork.repository;

import org.springframework.stereotype.Repository;
import ru.netology.cardwork.dto.Transfer;

@Repository
public class AccountsRepositoryMock implements AccountsRepository {


    @Override
    public void commitTransfer(Transfer transferToCommit) {

    }
}
