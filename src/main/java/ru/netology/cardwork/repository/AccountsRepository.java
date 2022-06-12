package ru.netology.cardwork.repository;

import ru.netology.cardwork.dto.Transfer;

public interface AccountsRepository {

    void commitTransfer(Transfer transferToCommit);
}
