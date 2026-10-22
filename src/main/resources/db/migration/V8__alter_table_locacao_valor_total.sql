-- O valor da locação passa a ser calculado pelo sistema (diária x quantidade de diárias)
-- O nome novo deixa claro que é o valor total do período, não um aluguel mensal.
ALTER TABLE locacao RENAME COLUMN valor_aluguel TO valor_total;