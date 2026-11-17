package com.crossborder.mall.service;

import com.crossborder.mall.dto.AddressSaveDTO;
import com.crossborder.mall.entity.CbAddress;

import java.util.List;

public interface AddressService {
    List<CbAddress> listMine();

    CbAddress getDefault();

    CbAddress create(AddressSaveDTO dto);

    CbAddress update(Long id, AddressSaveDTO dto);

    void delete(Long id);

    void setDefault(Long id);

    CbAddress requireOwned(Long id, Long userId);
}
