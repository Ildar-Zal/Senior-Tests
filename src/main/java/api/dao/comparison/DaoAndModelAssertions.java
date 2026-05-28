package api.dao.comparison;

import org.assertj.core.api.AbstractAssert;

public class DaoAndModelAssertions {

    private static final DaoComparator daoComparator = new DaoComparator();

    // ИСПРАВЛЕНИЕ: Заменили BaseModel на Object, чтобы метод принимал и списки (List) тоже
    public static DaoModelAssert assertThat(Object apiModel, Object daoModel) {
        return new DaoModelAssert(apiModel, daoModel);
    }

    public static class DaoModelAssert extends AbstractAssert<DaoModelAssert, Object> {
        private final Object apiModel; // ИСПРАВЛЕНИЕ: Меняем на Object
        private final Object daoModel;

        public DaoModelAssert(Object apiModel, Object daoModel) {
            super(apiModel, DaoModelAssert.class);
            this.apiModel = apiModel;
            this.daoModel = daoModel;
        }

        public DaoModelAssert match() {
            if (apiModel == null) {
                failWithMessage("API model should not be null");
            }

            if (daoModel == null) {
                failWithMessage("DAO model should not be null");
            }

            // Наш прокачанный DaoComparator теперь сам разберется,
            // объект это или список объектов (List)
            try {
                daoComparator.compare(apiModel, daoModel);
            } catch (AssertionError e) {
                failWithMessage(e.getMessage());
            }

            return this;
        }
    }
}