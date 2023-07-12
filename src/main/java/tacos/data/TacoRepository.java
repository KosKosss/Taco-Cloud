package tacos.data;


import org.springframework.data.repository.PagingAndSortingRepository;
import tacos.Taco;

import java.util.ArrayList;


public interface TacoRepository
        extends PagingAndSortingRepository<Taco, Long> {


}
