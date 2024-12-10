WITH rules AS ( -- Selects transactions that adhere to buyer rules
    SELECT transactionid, buyerid
    FROM (
        SELECT transactionid, description, buyerregex, buyerid 
        FROM transactions
        CROSS JOIN buyerrules
    )
    WHERE description
    LIKE buyerregex
), outlierFilter AS ( -- Adds replaces transaction from rules with outliers 
      SELECT transactionid, buyerid
      FROM rules
      WHERE NOT EXISTS ( -- Provides priority for outliers over rules
          SELECT 1
          FROM outliers
          WHERE rules.transactionid = outliers.transactionid 
      )
      UNION
      SELECT transactionid, buyerid
      FROM outliers
),  catFilter as ( -- creates a list of transactions that belongs to categories
      SELECT DISTINCT transactionid, catname
      FROM (
          SELECT transactionid, description, catregex, catname
          FROM transactions
          CROSS JOIN categories
      )
      WHERE description
      LIKE catregex
), whole AS ( -- Combines catfilter with outliers
    SELECT outlierFilter.transactionid AS transactionid, outlierFilter.buyerid AS buyerid, catFilter.catname AS catname
    FROM outlierFilter 
    LEFT JOIN catFilter
    ON catFilter.transactionid = outlierFilter.transactionid
)
SELECT DISTINCT transactions.transactionid as transactionid, date, description, value, balance, buyername, catfilter.catname
FROM transactions 
LEFT JOIN whole ON whole.transactionid = transactions.transactionid -- Adds whole to transactions table
LEFT JOIN buyers ON whole.buyerid = buyers.buyerid -- Give transaction table buyer names
LEFT JOIN catfilter ON transactions.transactionid = catfilter.transactionid; -- adds category names to final select


