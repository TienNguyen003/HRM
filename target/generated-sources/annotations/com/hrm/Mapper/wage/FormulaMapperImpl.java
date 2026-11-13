package com.hrm.Mapper.wage;

import com.hrm.Entity.wage.Formula;
import com.hrm.dto.request.wage.FormulaRequest;
import com.hrm.dto.response.wage.FormulaResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.3 (Oracle Corporation)"
)
@Component
public class FormulaMapperImpl implements FormulaMapper {

    @Override
    public Formula toFormula(FormulaRequest request) {
        if ( request == null ) {
            return null;
        }

        Formula.FormulaBuilder formula = Formula.builder();

        formula.name( request.getName() );
        formula.salaryFormula( request.getSalaryFormula() );

        return formula.build();
    }

    @Override
    public FormulaResponse toFormulaResponse(Formula formula) {
        if ( formula == null ) {
            return null;
        }

        FormulaResponse.FormulaResponseBuilder formulaResponse = FormulaResponse.builder();

        formulaResponse.id( formula.getId() );
        formulaResponse.name( formula.getName() );
        formulaResponse.salaryFormula( formula.getSalaryFormula() );
        formulaResponse.status( formula.getStatus() );

        return formulaResponse.build();
    }

    @Override
    public void updateFormula(Formula formula, FormulaRequest request) {
        if ( request == null ) {
            return;
        }

        formula.setName( request.getName() );
        formula.setSalaryFormula( request.getSalaryFormula() );
    }
}
