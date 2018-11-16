package io.mywish.airdrop.repositories;

import io.mywish.airdrop.model.WishTransfer;
import org.springframework.data.repository.CrudRepository;

public interface WishTransferRepository extends CrudRepository<WishTransfer, Integer> {
}
