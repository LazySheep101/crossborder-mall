package com.crossborder.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.crossborder.mall.common.ResultCode;
import com.crossborder.mall.dto.AddressSaveDTO;
import com.crossborder.mall.entity.CbAddress;
import com.crossborder.mall.exception.BusinessException;
import com.crossborder.mall.mapper.CbAddressMapper;
import com.crossborder.mall.service.AddressService;
import com.crossborder.mall.utils.UserHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final CbAddressMapper addressMapper;

    @Override
    public List<CbAddress> listMine() {
        Long userId = UserHolder.requireUser().getId();
        return addressMapper.selectList(new LambdaQueryWrapper<CbAddress>()
                .eq(CbAddress::getUserId, userId)
                .orderByDesc(CbAddress::getIsDefault)
                .orderByDesc(CbAddress::getId));
    }

    @Override
    public CbAddress getDefault() {
        Long userId = UserHolder.requireUser().getId();
        CbAddress addr = addressMapper.selectOne(new LambdaQueryWrapper<CbAddress>()
                .eq(CbAddress::getUserId, userId)
                .eq(CbAddress::getIsDefault, 1)
                .last("LIMIT 1"));
        if (addr == null) {
            List<CbAddress> list = listMine();
            return list.isEmpty() ? null : list.get(0);
        }
        return addr;
    }

    @Override
    @Transactional
    public CbAddress create(AddressSaveDTO dto) {
        Long userId = UserHolder.requireUser().getId();
        boolean makeDefault = Boolean.TRUE.equals(dto.getIsDefault()) || countOf(userId) == 0;
        if (makeDefault) {
            clearDefault(userId);
        }
        CbAddress address = toEntity(dto);
        address.setUserId(userId);
        address.setIsDefault(makeDefault ? 1 : 0);
        addressMapper.insert(address);
        return address;
    }

    @Override
    @Transactional
    public CbAddress update(Long id, AddressSaveDTO dto) {
        Long userId = UserHolder.requireUser().getId();
        CbAddress existing = requireOwned(id, userId);
        existing.setReceiverName(dto.getReceiverName());
        existing.setReceiverPhone(dto.getReceiverPhone());
        existing.setProvince(nullToEmpty(dto.getProvince()));
        existing.setCity(nullToEmpty(dto.getCity()));
        existing.setDistrict(nullToEmpty(dto.getDistrict()));
        existing.setDetail(dto.getDetail());
        if (Boolean.TRUE.equals(dto.getIsDefault())) {
            clearDefault(userId);
            existing.setIsDefault(1);
        }
        addressMapper.updateById(existing);
        return existing;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Long userId = UserHolder.requireUser().getId();
        CbAddress existing = requireOwned(id, userId);
        addressMapper.deleteById(existing.getId());
        if (existing.getIsDefault() != null && existing.getIsDefault() == 1) {
            List<CbAddress> rest = listMine();
            if (!rest.isEmpty()) {
                setDefault(rest.get(0).getId());
            }
        }
    }

    @Override
    @Transactional
    public void setDefault(Long id) {
        Long userId = UserHolder.requireUser().getId();
        requireOwned(id, userId);
        clearDefault(userId);
        addressMapper.update(null, new LambdaUpdateWrapper<CbAddress>()
                .eq(CbAddress::getId, id)
                .set(CbAddress::getIsDefault, 1));
    }

    @Override
    public CbAddress requireOwned(Long id, Long userId) {
        CbAddress address = addressMapper.selectById(id);
        if (address == null || !userId.equals(address.getUserId())) {
            throw new BusinessException(ResultCode.NOT_FOUND, "地址不存在");
        }
        return address;
    }

    private long countOf(Long userId) {
        return addressMapper.selectCount(new LambdaQueryWrapper<CbAddress>()
                .eq(CbAddress::getUserId, userId));
    }

    private void clearDefault(Long userId) {
        addressMapper.update(null, new LambdaUpdateWrapper<CbAddress>()
                .eq(CbAddress::getUserId, userId)
                .set(CbAddress::getIsDefault, 0));
    }

    private CbAddress toEntity(AddressSaveDTO dto) {
        CbAddress address = new CbAddress();
        address.setReceiverName(dto.getReceiverName());
        address.setReceiverPhone(dto.getReceiverPhone());
        address.setProvince(nullToEmpty(dto.getProvince()));
        address.setCity(nullToEmpty(dto.getCity()));
        address.setDistrict(nullToEmpty(dto.getDistrict()));
        address.setDetail(dto.getDetail());
        return address;
    }

    private String nullToEmpty(String value) {
        return StringUtils.hasText(value) ? value : "";
    }
}
