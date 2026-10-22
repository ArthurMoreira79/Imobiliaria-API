-- A data de fim passou a ser obrigatória (sem ela não dá para calcular as diárias).
-- A entidade já exigia com @NotNull; agora o banco também garante a regra.
ALTER TABLE locacao MODIFY data_fim DATE NOT NULL;
