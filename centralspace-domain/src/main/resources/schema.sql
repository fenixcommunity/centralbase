-- run first during app starting (Primary database)

CREATE FUNCTION table_exist(schema text, tbl text)
    RETURNS boolean AS
$$
DECLARE
    exist boolean;
BEGIN
        SELECT CASE WHEN (
            SELECT ...
        ) THEN 1
                ELSE 0 END into exist;
    SELECT to_regclass($1 || '.' || $2) is not null into exist;
    RETURN exist;
END;
$$ LANGUAGE plpgsql
    SECURITY DEFINER
    SET search_path = admin, pg_temp;
--
SELECT CASE
           WHEN (
               table_exist('public', 'account')
               ) THEN (select a.login from account a limit 1)
           ELSE 'not exist' END;


select 1 from account;