package mkoutra.birthdaykeeper.core;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * A wrapper class for {@link Page} objects, providing a subset of fields
 * from the {@link Page} for simplified access or serialization.
 *
 * @param <T> the type of content contained within the {@link Page}.
 */
@Getter
@Setter
public class Paginated<T> {
    List<T> content;
    long totalElements;
    int pageSize;
    int totalPages;
    int numberOfElements;
    int currentPage;

    public Paginated(Page<T> page) {
        this.content = page.getContent();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.numberOfElements = page.getNumberOfElements();
        this.currentPage = page.getNumber();
        this.pageSize = page.getSize();
    }
}
