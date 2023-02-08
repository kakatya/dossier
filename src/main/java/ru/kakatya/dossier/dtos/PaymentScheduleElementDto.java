package ru.kakatya.dossier.dtos;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
public class PaymentScheduleElementDto implements Serializable {
    private Integer number;
    private LocalDate date;
    private BigDecimal totalPayment;
    private BigDecimal interestPayment;
    private BigDecimal debtPayment;
    private BigDecimal remainingDebt;

    @Override
    public String toString() {
        return "\nnumber= " + number + "\n" +
                "date= " + date + "\n" +
                "totalPayment= " + totalPayment + "\n" +
                "interestPayment= " + interestPayment + "\n" +
                "debtPayment= " + debtPayment + "\n" +
                "remainingDebt= " + remainingDebt + "\n";
    }
}
