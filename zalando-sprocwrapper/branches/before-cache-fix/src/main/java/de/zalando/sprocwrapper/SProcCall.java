package de.zalando.sprocwrapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author  jmussler
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface SProcCall {

    public static enum AdvisoryLock {
        NO_LOCK(0L),

        // values 1 to 34 have been copied from de.zalando.dbutils.sproc.LockingSproc
        GET_CAPTURE_QUEUE_ITEM(1L),
        GET_PENDING_MAILS(2L),
        GET_UNSENT_GIFT_VOUCHERS(3L),
        GET_DOCDATA_STOCK_MUTATIONS(4L),
        GET_DOCDATA_RETURN_ORDERS(5L),
        GET_PRICE_COMPARISON_SERVICE_AND_SET_TO_PROCESSING(6L),
        EXPORT_UPDATED_ARTICLES_TO_ZALOS(7L),
        PROCESS_PURCHASE_ORDERS_FOR_DOCDATA(8L),
        EXPORT_PURCHASE_ORDERS_TO_ZALOS(9L),
        PROCESS_SUPPLIERS_FOR_DOCDATA(10L),
        EXPORT_SUPPLIERS_TO_ZALOS(11L),
        IMPORT_ZALOS_STOCK_MUTATIONS(12L),
        GET_UPDATED_ARTICLE_STOCKS_SHOP(13L),
        GET_UPDATER_EVENTS_AND_SET_TO_PROCESSING(14L),
        EXPORT_SALES_ORDERS_TO_ZALOS(15L),
        SET_ARTICLE_EXPORTED_TO_ZALOS_STATI(16L),
        GET_SHIPPING_CONFIRMATIONS(17L),
        GET_UPDATED_ARTICLE_STOCKS_EXPORTER(18L),
        GET_UPDATED_ARTICLE_STOCKS_LOUNGE(19L),
        IMPORT_ZALOS_SHIPPING_CONFIRMATIONS(20L),
        IMPORT_ZALOS_SHIPPING_STATS_CHANGE(21L),
        IMPORT_ZALOS_SUPPLIER_STOCK_MUTATIONS(22L),
        GET_AND_UPDATE_FIA_REQUEST(23L),
        IMPORT_ZALOS_RETURN_ORDERS(24L),
        IMPORT_ZALOS_OUTBOUND_SCANS(25L),
        EXPORT_SALES_ORDERS_TO_PARTNER_SERVICE(26L),
        IMPORT_PARTNER_SHIPPING_STATS_CHANGES(27L),
        IMPORT_PARTNER_SHIPPING_CONFIRMATIONS(28L),
        IMPORT_PRICES_FROM_PARTNER_SERVICE(29L),
        IMPORT_STOCKS_FROM_PARTNER_SERVICE(30L),
        GET_PARTNER_ARTICLES(31L),
        GET_ORDERS_BY_STATUS_AND_SET_NEW_STATUS(32L),
        GET_ERP_CALLS_AND_SET_TO_PROCESSING(33L),
        GET_STOCK_SERVICE_CALLS(34L),
        ARTICLE_SIMPLE_WATCHER_FETCH_QUEUES_AND_MARK_PROCESSING(35L),
        IMPORT_COMMISSIONS(36L),
        IMPORT_INBOUND_TOURS(37L),
        GET_ZALOS_DEVICE(38L),
        ZALOS_PRINT_LIST_PREPARE(39L),
        ZALOS_PRINT_LIST_POSITION_PREPARE(40L),
        GET_ZALOS_INBOUND_BATCHING(41L),
        ZALOS_PRINT_LIST_POSITION_GET_NEXT_DECENTRALIZED(42L),
        ZALOS_PICK_LIST_START_SEARCH(43L),
        ZALOS_COMMISSIONS_FOR_BATCHER(44L),

        /*
         * The values following now don't exist in LockingSproc. To allow adding new values to
         * de.zalando.dbutils.sproc.LockingSproc though we continue counting with a considerably higher value
         */
        GET_PENDING_BATCH_MAILS(1001L),
        EAN_GET_NEXT_EAN(1002L),
        ZALOS_EXPORT_OUTBOUND_TOURS(1003L),
        ZALOS_INBOUND_TOUR_ARRIVAL(1004L);

        private AdvisoryLock(final long sprocId) {
            this.sprocId = sprocId;
        }

        private final long sprocId;

        public long getSprocId() {
            return sprocId;
        }

    }

    public static enum Validate {
        AS_DEFINED_IN_SERVICE,
        YES,
        NO
    }

    public static enum WriteTransaction {
        USE_FROM_SERVICE,
        NONE,
        ONE_PHASE,
        TWO_PHASE
    }

    String name() default "";

    String sql() default "";

    Class<?> shardStrategy() default Void.class;

    /**
     * whether the stored procedure should be called on all shards --- results are concatenated together.
     *
     * @return
     */
    boolean runOnAllShards() default false;

    /**
     * whether the stored procedure should be called on all shards --- return the first result found.
     *
     * @return
     */
    boolean searchShards() default false;

    /**
     * run sproc on multiple shards in parallel?
     *
     * @return
     */
    boolean parallel() default false;

    /**
     * flag this stored procedure call as read only: read only sprocs may run in cases were writing calls would not be
     * allowed (maintenance, migration, ..)
     *
     * @return
     */
    boolean readOnly() default true;

    /**
     * Defines how sharded writes will be handled. If set to {@link WriteTransaction#NONE}, no transaction context will
     * be created. If set to {@link WriteTransaction#ONE_PHASE}, all errors during the sproc call will be rolled back.
     * If set to {@link WriteTransaction#TWO_PHASE}, all errors during sproc call and "prepare transaction" are rolled
     * back. In the last case, the Postgres instance must be configured to manage 2-phase-commits (XA).
     */
    WriteTransaction shardedWriteTransaction() default WriteTransaction.USE_FROM_SERVICE;

    Class<?> resultMapper() default Void.class;

    long timeoutInMilliSeconds() default 0;

    AdvisoryLock adivsoryLockType() default AdvisoryLock.NO_LOCK;

    Validate validate() default Validate.AS_DEFINED_IN_SERVICE;
}
