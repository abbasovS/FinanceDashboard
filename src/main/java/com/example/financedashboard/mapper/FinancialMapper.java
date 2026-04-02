package com.example.financedashboard.mapper;

import com.example.financedashboard.dto.request.FinancialRecordRequest;
import com.example.financedashboard.dto.response.FinancialRecordResponse;
import com.example.financedashboard.model.entity.FinancialRecord;
import com.example.financedashboard.model.entity.User;
import org.springframework.stereotype.Component;

@Component
public class FinancialMapper {
    public FinancialRecord toEntity(FinancialRecordRequest request, User user) {
        FinancialRecord record = new FinancialRecord();
        record.setAmount(request.amount());
        record.setType(request.type());
        record.setCategory(request.category());
        record.setDate(request.date());
        record.setNotes(request.notes());
        record.setUser(user);
        return record;
    }

    public FinancialRecordResponse toResponse(FinancialRecord record) {
        return new FinancialRecordResponse(
                record.getId(), record.getAmount(), record.getType(),
                record.getCategory(), record.getDate(), record.getNotes(),
                record.getUser().getId(), record.getCreatedAt(),
                record.getUpdatedAt()
        );
    }
}