import os
import sys
from datetime import datetime

from reportlab.lib import colors
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import cm
from reportlab.pdfgen import canvas
from reportlab.platypus import (
    SimpleDocTemplate,
    Paragraph,
    Spacer,
    Table,
    TableStyle,
    PageBreak,
    KeepTogether,
    HRFlowable,
)


class NumberedCanvas(canvas.Canvas):
    """Canvas de duas passagens para calcular o total real de páginas e adicionar cabeçalho e rodapé profissionais."""

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self._saved_page_states = []

    def showPage(self):
        self._saved_page_states.append(dict(self.__dict__))
        self._startPage()

    def save(self):
        num_pages = len(self._saved_page_states)
        for state in self._saved_page_states:
            self.__dict__.update(state)
            self.draw_page_decorations(num_pages)
            super().showPage()
        super().save()

    def draw_page_decorations(self, page_count):
        # Na capa (página 1), não desenha cabeçalho nem rodapé
        if self._pageNumber == 1:
            return

        self.saveState()
        self.setFont("Helvetica", 8)
        self.setFillColor(colors.HexColor("#64748b"))

        # Cabeçalho superior
        self.drawString(54, 800, "ProductManager — Sistema de Gestão de Produtos (Java Puro & REST API)")
        self.drawRightString(558, 800, "Documentação Técnica do Sistema")
        self.setStrokeColor(colors.HexColor("#e2e8f0"))
        self.setLineWidth(0.6)
        self.line(54, 792, 558, 792)

        # Rodapé inferior
        self.drawString(54, 32, "Disciplina: Aplicações para Internet — Padrão MVC com Controller, Service e DAO")
        self.drawRightString(558, 32, f"Página {self._pageNumber} de {page_count}")
        self.line(54, 44, 558, 44)

        self.restoreState()


def create_pdf(output_filename):
    doc = SimpleDocTemplate(
        output_filename,
        pagesize=A4,
        leftMargin=54,
        rightMargin=54,
        topMargin=54,
        bottomMargin=54,
    )

    styles = getSampleStyleSheet()

    # Estilos customizados
    primary_color = colors.HexColor("#0f172a")
    brand_color = colors.HexColor("#4f46e5")
    text_muted = colors.HexColor("#475569")
    bg_light = colors.HexColor("#f8fafc")

    title_style = ParagraphStyle(
        "CoverTitle",
        parent=styles["Normal"],
        fontName="Helvetica-Bold",
        fontSize=24,
        leading=30,
        textColor=primary_color,
        alignment=0,
    )

    subtitle_style = ParagraphStyle(
        "CoverSubtitle",
        parent=styles["Normal"],
        fontName="Helvetica",
        fontSize=12,
        leading=16,
        textColor=brand_color,
        alignment=0,
    )

    h1_style = ParagraphStyle(
        "Heading1_Custom",
        parent=styles["Normal"],
        fontName="Helvetica-Bold",
        fontSize=15,
        leading=19,
        textColor=primary_color,
        spaceBefore=14,
        spaceAfter=6,
        keepWithNext=True,
    )

    h2_style = ParagraphStyle(
        "Heading2_Custom",
        parent=styles["Normal"],
        fontName="Helvetica-Bold",
        fontSize=11.5,
        leading=15,
        textColor=brand_color,
        spaceBefore=10,
        spaceAfter=4,
        keepWithNext=True,
    )

    body_style = ParagraphStyle(
        "Body_Custom",
        parent=styles["Normal"],
        fontName="Helvetica",
        fontSize=9.2,
        leading=13.5,
        textColor=colors.HexColor("#1e293b"),
        spaceAfter=6,
    )

    bullet_style = ParagraphStyle(
        "Bullet_Custom",
        parent=body_style,
        leftIndent=14,
        bulletIndent=6,
        spaceAfter=3,
    )

    code_style = ParagraphStyle(
        "Code_Custom",
        parent=styles["Normal"],
        fontName="Courier",
        fontSize=8,
        leading=11,
        textColor=colors.HexColor("#f8fafc"),
    )

    table_header_style = ParagraphStyle(
        "TableHeader",
        parent=styles["Normal"],
        fontName="Helvetica-Bold",
        fontSize=8.5,
        leading=11,
        textColor=colors.white,
    )

    table_cell_style = ParagraphStyle(
        "TableCell",
        parent=styles["Normal"],
        fontName="Helvetica",
        fontSize=8.2,
        leading=11,
        textColor=colors.HexColor("#1e293b"),
    )

    callout_text_style = ParagraphStyle(
        "CalloutText",
        parent=styles["Normal"],
        fontName="Helvetica",
        fontSize=8.8,
        leading=12.5,
        textColor=colors.HexColor("#1e293b"),
    )

    story = []

    # ==================== CAPA / CABEÇALHO INICIAL ====================
    story.append(Spacer(1, 15))
    badge_table = Table(
        [[Paragraph("<b>PROJETO ACADÊMICO — APLICAÇÕES PARA INTERNET</b>", ParagraphStyle(
            "BadgeText", fontName="Helvetica-Bold", fontSize=8, textColor=colors.HexColor("#4338ca")
        ))]],
        colWidths=[504],
    )
    badge_table.setStyle(TableStyle([
        ("BACKGROUND", (0, 0), (-1, -1), colors.HexColor("#eef2ff")),
        ("PADDING", (0, 0), (-1, -1), 6),
        ("ALIGN", (0, 0), (-1, -1), "LEFT"),
        ("VALIGN", (0, 0), (-1, -1), "MIDDLE"),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 5),
    ]))
    story.append(badge_table)
    story.append(Spacer(1, 12))

    story.append(Paragraph("ProductManager — Sistema de Gestão de Produtos", title_style))
    story.append(Spacer(1, 4))
    story.append(Paragraph("Documentação Técnica Completa da Arquitetura MVC, DAO e API RESTful em Java Puro", subtitle_style))
    story.append(Spacer(1, 10))

    meta_text = (
        "<b>Tema do Sistema:</b> Gestão de Estoque e Catálogo de Produtos &nbsp;|&nbsp; "
        "<b>Stack:</b> Java Puro (HttpServer + JDBC), MySQL, HTML5/CSS3/JS Puro<br/>"
        "<b>Data de Conclusão:</b> Novembro/2026 &nbsp;|&nbsp; "
        "<b>Arquitetura:</b> Padrão MVC com Separação em Controller, Service e DAO"
    )
    story.append(Paragraph(meta_text, ParagraphStyle("Meta", parent=body_style, fontSize=8.5, textColor=text_muted)))
    story.append(Spacer(1, 10))
    story.append(HRFlowable(width="100%", thickness=1.5, color=colors.HexColor("#e2e8f0"), spaceBefore=2, spaceAfter=14))

    # ==================== SEÇÃO 1: RESUMO EXECUTIVO ====================
    story.append(Paragraph("1. Resumo Executivo e Propósito do Sistema", h1_style))
    story.append(Paragraph(
        "O <b>ProductManager</b> é uma solução corporativa completa projetada para gerenciar o catálogo mercadológico e o saldo de inventário da empresa <b>DMAXTECH Systems</b>. "
        "O projeto foi integralmente construído segundo as diretrizes acadêmicas de <b>Aplicações para Internet</b>, atendendo aos seguintes pilares de engenharia:",
        body_style,
    ))

    story.append(Paragraph("• <b>Backend em Java Puro (Vanilla Java):</b> Sem o uso de frameworks como Spring Boot, Hibernate, Spring Data JPA ou Jakarta EE. Todo o controle de ciclo de vida HTTP é realizado através do servidor nativo <code>com.sun.net.httpserver.HttpServer</code> do Java SE.", bullet_style))
    story.append(Paragraph("• <b>Persistência com JDBC Puro:</b> Todas as operações de leitura, inclusão, atualização e exclusão no banco de dados MySQL são executadas diretamente via <code>java.sql.Connection</code>, <code>PreparedStatement</code> e <code>ResultSet</code>, garantindo eficiência, isolamento de escopo e proteção contra SQL Injection.", bullet_style))
    story.append(Paragraph("• <b>Arquitetura MVC com Camadas Obrigatórias:</b> Divisão estrita em <b>Controller</b> (requisições e status HTTP), <b>Service</b> (regras de negócio e validações) e <b>DAO</b> (acesso atômico ao MySQL).", bullet_style))
    story.append(Paragraph("• <b>Frontend 100% Puro (Sem Frameworks):</b> Desenvolvido exclusivamente em HTML5 semântico, CSS3 moderno responsivo e JavaScript Vanilla com a Fetch API nativa. Não requer React, Tailwind CLI, Angular ou Node.js.", bullet_style))
    story.append(Paragraph("• <b>Segurança e Autenticação Criptográfica:</b> Login com senhas criptografadas em hash <b>BCrypt</b> e emissão de tokens digitais assinados com <b>HMAC-SHA256</b> nativo.", bullet_style))
    story.append(Spacer(1, 10))

    # ==================== SEÇÃO 2: O QUE É A API E COMO FUNCIONA ====================
    story.append(Paragraph("2. O que é a API e qual é o seu Papel no Sistema?", h1_style))
    story.append(Paragraph(
        "A <b>API RESTful (Application Programming Interface)</b> é o coração funcional da aplicação. Ela atua como uma interface padronizada que expõe as funcionalidades de negócio através de requisições HTTP seguras, desacoplando completamente a camada de apresentação (View / Navegador) do banco de dados relacional.",
        body_style,
    ))

    # Box de destaque explicativo
    callout_data = [
        [
            Paragraph(
                "<b>Por que a API foi construída e o que ela faz?</b><br/>"
                "1. <b>Segurança e Integridade:</b> O navegador nunca se conecta diretamente ao MySQL. A API atua como um guardião central, validando tokens de autorização, verificando credenciais criptografadas e impedindo valores inconsistentes.<br/>"
                "2. <b>Processamento Centralizado de Regras:</b> A API garante que o catálogo não receba itens com estoque negativo, preços inválidos ou categorias em branco.<br/>"
                "3. <b>Comunicação via Protocolo REST:</b> Todas as ações correspondem a verbos semânticos do protocolo HTTP (<code>GET</code> para leitura, <code>POST</code> para criação, <code>PUT</code> para edição e <code>DELETE</code> para exclusão).",
                callout_text_style,
            )
        ]
    ]
    callout_table = Table(callout_data, colWidths=[504])
    callout_table.setStyle(TableStyle([
        ("BACKGROUND", (0, 0), (-1, -1), colors.HexColor("#f1f5f9")),
        ("BOX", (0, 0), (-1, -1), 1, colors.HexColor("#cbd5e1")),
        ("PADDING", (0, 0), (-1, -1), 8),
        ("VALIGN", (0, 0), (-1, -1), "TOP"),
    ]))
    story.append(callout_table)
    story.append(Spacer(1, 10))

    story.append(Paragraph("Como a API funciona nos Containers Docker e Localmente:", h2_style))
    story.append(Paragraph(
        "No arquivo <code>docker-compose.yml</code>, o sistema está configurado para operar em 3 containers desacoplados que conversam através de uma rede interna virtual (<code>product-manager-net</code>):<br/>"
        "• <b>Container <code>mysql</code> (porta 3306):</b> Instância do MySQL 8.0 com inicialização automática do banco <code>product_manager</code> via <code>database/init.sql</code> e volume persistente.<br/>"
        "• <b>Container <code>backend</code> (porta 8080):</b> Executa a nossa API em Java Puro compilada em um Fat JAR executável independente.<br/>"
        "• <b>Container <code>frontend</code> (porta 3000):</b> Servidor web leve Nginx que entrega os arquivos estáticos (HTML/CSS/JS puro).<br/>"
        "<i>Em desenvolvimento local sem Docker, o próprio servidor Java Puro (HttpServer) contém o controlador <code>StaticFileController</code>, servindo o frontend puro diretamente na porta 8080.</i>",
        body_style,
    ))
    story.append(Spacer(1, 10))

    # ==================== SEÇÃO 3: MODELAGEM DE DADOS (MYSQL) ====================
    story.append(Paragraph("3. Modelagem de Dados e Entidades (MySQL)", h1_style))
    story.append(Paragraph(
        "O modelo relacional é composto por duas entidades essenciais mapeadas de forma limpa em POJOs puros:",
        body_style,
    ))

    # Tabela Product
    product_table_data = [
        [Paragraph("Campo", table_header_style), Paragraph("Tipo MySQL", table_header_style), Paragraph("Tipo Java", table_header_style), Paragraph("Descrição e Regras de Validação", table_header_style)],
        [Paragraph("<code>id</code>", table_cell_style), Paragraph("BIGINT PK", table_cell_style), Paragraph("Long", table_cell_style), Paragraph("Chave primária numérica gerada automaticamente (AUTO_INCREMENT)", table_cell_style)],
        [Paragraph("<code>name</code>", table_cell_style), Paragraph("VARCHAR(120)", table_cell_style), Paragraph("String", table_cell_style), Paragraph("Nome do produto. Obrigatório, não nulo, máx. 120 caracteres", table_cell_style)],
        [Paragraph("<code>description</code>", table_cell_style), Paragraph("VARCHAR(500)", table_cell_style), Paragraph("String", table_cell_style), Paragraph("Descrição e especificações do produto (opcional)", table_cell_style)],
        [Paragraph("<code>category</code>", table_cell_style), Paragraph("VARCHAR(60)", table_cell_style), Paragraph("String", table_cell_style), Paragraph("Categoria mercadológica. Obrigatória, não nula, máx. 60 caracteres", table_cell_style)],
        [Paragraph("<code>price</code>", table_cell_style), Paragraph("DECIMAL(10,2)", table_cell_style), Paragraph("BigDecimal", table_cell_style), Paragraph("Preço unitário de venda em R$. Obrigatório e não negativo", table_cell_style)],
        [Paragraph("<code>stock</code>", table_cell_style), Paragraph("INT", table_cell_style), Paragraph("Integer", table_cell_style), Paragraph("Saldo em estoque. Obrigatório, número inteiro maior ou igual a zero", table_cell_style)],
        [Paragraph("<code>created_at</code>", table_cell_style), Paragraph("DATETIME", table_cell_style), Paragraph("LocalDateTime", table_cell_style), Paragraph("Timestamp de inserção do registro no banco", table_cell_style)],
        [Paragraph("<code>updated_at</code>", table_cell_style), Paragraph("DATETIME", table_cell_style), Paragraph("LocalDateTime", table_cell_style), Paragraph("Timestamp da última modificação do registro", table_cell_style)],
    ]
    t_prod = Table(product_table_data, colWidths=[70, 75, 75, 284])
    t_prod.setStyle(TableStyle([
        ("BACKGROUND", (0, 0), (-1, 0), primary_color),
        ("GRID", (0, 0), (-1, -1), 0.5, colors.HexColor("#cbd5e1")),
        ("PADDING", (0, 0), (-1, -1), 4.5),
        ("ROWBACKGROUNDS", (0, 1), (-1, -1), [colors.white, bg_light]),
    ]))
    story.append(t_prod)
    story.append(Spacer(1, 10))

    story.append(Paragraph(
        "<b>Entidade de Segurança (User):</b> Tabela <code>users</code> composta por <code>id</code> (BIGINT PK), <code>username</code> (VARCHAR(60) UNIQUE), <code>password</code> (VARCHAR(255) contendo o hash BCrypt) e <code>role</code> (VARCHAR(20)). "
        "A aplicação inclui o seeder automático <code>DataSeeder</code> que garante a existência do usuário administrador padrão (<b>admin / admin123</b>) na inicialização.",
        body_style,
    ))
    story.append(Spacer(1, 10))

    # Quebra de página para manter a arquitetura unificada
    story.append(PageBreak())

    # ==================== SEÇÃO 4: ARQUITETURA MVC + DAO + SERVICE ====================
    story.append(Paragraph("4. Arquitetura MVC com Separação Obrigatória em Camadas", h1_style))
    story.append(Paragraph(
        "A aplicação adota com rigor o princípio de responsabilidade única (Single Responsibility Principle) e a divisão em três camadas lógicas independentes:",
        body_style,
    ))

    # Diagrama ASCII em bloco escuro
    diagram_text = (
        "+-------------------------------------------------------------------------+\n"
        "|                 CLIENTE HTTP / NAVEGADOR (FRONTEND PURO)                |\n"
        "+------------------------------------+------------------------------------+\n"
        "                                     |  Requisicoes HTTP (Fetch API)\n"
        "                                     v\n"
        "+-------------------------------------------------------------------------+\n"
        "|                       CAMADA CONTROLLER (Web / REST)                    |\n"
        "|  * ProductController.java  * AuthController.java  * StaticFileController|\n"
        "|  - Implementa HttpHandler nativo do Java SE (sem Spring MVC)            |\n"
        "|  - Mapeia rotas e verbos HTTP (GET, POST, PUT, DELETE, OPTIONS)         |\n"
        "|  - Extrai e valida o token Bearer no cabecalho Authorization            |\n"
        "|  - Serializa/desserializa JSON via Gson e emite status semanticos       |\n"
        "+------------------------------------+------------------------------------+\n"
        "                                     |  Objetos de Dominio / DTOs\n"
        "                                     v\n"
        "+-------------------------------------------------------------------------+\n"
        "|                       CAMADA SERVICE (Regras de Negocio)                |\n"
        "|  * ProductService.java     * AuthService.java                           |\n"
        "|  - Validacoes rigorosas de integridade (preco >= 0, estoque >= 0)       |\n"
        "|  - Autenticacao com BCrypt.checkpw e assinatura HMAC-SHA256             |\n"
        "|  - Trata excecoes de dominio (ValidationException, ResourceNotFound)    |\n"
        "|  - E o UNICO ponto autorizado a acessar o DAO                           |\n"
        "+------------------------------------+------------------------------------+\n"
        "                                     |  Operacoes de Persistencia\n"
        "                                     v\n"
        "+-------------------------------------------------------------------------+\n"
        "|                        CAMADA DAO (Data Access Object)                  |\n"
        "|  * ProductDAO.java (Interface)       * ProductDAOImpl.java (JDBC)       |\n"
        "|  * UserDAO.java (Interface)          * UserDAOImpl.java (JDBC)          |\n"
        "|  - Isolamento completo do acesso ao banco de dados MySQL                |\n"
        "|  - PreparedStatement parametrizados contra SQL Injection                |\n"
        "|  - Mapeamento explícito de ResultSet para Product e User                |\n"
        "+------------------------------------+------------------------------------+\n"
        "                                     |  Comandos SQL / Driver JDBC\n"
        "                                     v\n"
        "+-------------------------------------------------------------------------+\n"
        "|                           BANCO DE DADOS (MySQL)                        |\n"
        "|                     Tabelas: \"products\" e \"users\"                       |\n"
        "+-------------------------------------------------------------------------+"
    )
    diagram_table = Table([[Paragraph(f"<pre>{diagram_text}</pre>", code_style)]], colWidths=[504])
    diagram_table.setStyle(TableStyle([
        ("BACKGROUND", (0, 0), (-1, -1), colors.HexColor("#0f172a")),
        ("PADDING", (0, 0), (-1, -1), 8),
    ]))
    story.append(diagram_table)
    story.append(Spacer(1, 12))

    story.append(Paragraph("Responsabilidades Detalhadas de Cada Componente:", h2_style))
    story.append(Paragraph("• <b>ProductDAO / ProductDAOImpl:</b> Interface e implementação JDBC pura. Todos os métodos (<code>findAll</code>, <code>findById</code>, <code>searchByName</code>, <code>save</code>, <code>update</code>, <code>deleteById</code>) utilizam blocos <code>try-with-resources</code> para encerramento determinístico das conexões. Nenhum comando SQL vaza para as camadas superiores.", bullet_style))
    story.append(Paragraph("• <b>ProductService:</b> Centraliza a validação lógica dos dados. Se o usuário tentar cadastrar um item sem nome ou com preço negativo, o serviço monta um mapa detalhado de erros e dispara <code>ValidationException</code>.", bullet_style))
    story.append(Paragraph("• <b>AuthService:</b> Realiza a verificação criptográfica da senha através do <code>BCrypt.checkpw</code>. Se as credenciais estiverem corretas, gera um token estruturado assinado com uma chave secreta via <code>Mac.getInstance('HmacSHA256')</code>.", bullet_style))
    story.append(Paragraph("• <b>ProductController & AuthController:</b> Handlers que implementam a interface <code>com.sun.net.httpserver.HttpHandler</code> nativa do Java, sem necessidade de Servlets tradicionais ou Spring Controllers.", bullet_style))
    story.append(Spacer(1, 10))

    # ==================== SEÇÃO 5: CATÁLOGO DE ENDPOINTS ====================
    story.append(Paragraph("5. Catálogo de Endpoints da API RESTful", h1_style))
    story.append(Paragraph(
        "A tabela a seguir apresenta todas as rotas expostas pela API RESTful, o método HTTP, a exigência de token e os códigos de resposta semânticos:",
        body_style,
    ))

    endpoints_data = [
        [Paragraph("Verbo", table_header_style), Paragraph("Rota", table_header_style), Paragraph("Auth", table_header_style), Paragraph("Descrição do Endpoint", table_header_style), Paragraph("Status Sucesso", table_header_style)],
        [Paragraph("<b>POST</b>", table_cell_style), Paragraph("<code>/api/auth/login</code>", table_cell_style), Paragraph("Pública", table_cell_style), Paragraph("Autentica operador e gera token HMAC-SHA256", table_cell_style), Paragraph("200 OK", table_cell_style)],
        [Paragraph("<b>GET</b>", table_cell_style), Paragraph("<code>/api/products</code>", table_cell_style), Paragraph("Bearer", table_cell_style), Paragraph("Lista todos os produtos cadastrados no estoque", table_cell_style), Paragraph("200 OK", table_cell_style)],
        [Paragraph("<b>GET</b>", table_cell_style), Paragraph("<code>/api/products?search=t</code>", table_cell_style), Paragraph("Bearer", table_cell_style), Paragraph("Filtra produtos cujo nome contenha o termo 't'", table_cell_style), Paragraph("200 OK", table_cell_style)],
        [Paragraph("<b>GET</b>", table_cell_style), Paragraph("<code>/api/products/{id}</code>", table_cell_style), Paragraph("Bearer", table_cell_style), Paragraph("Retorna os dados detalhados de um produto por ID", table_cell_style), Paragraph("200 OK / 404", table_cell_style)],
        [Paragraph("<b>POST</b>", table_cell_style), Paragraph("<code>/api/products</code>", table_cell_style), Paragraph("Bearer", table_cell_style), Paragraph("Cadastra um novo produto no banco de dados", table_cell_style), Paragraph("201 Created", table_cell_style)],
        [Paragraph("<b>PUT</b>", table_cell_style), Paragraph("<code>/api/products/{id}</code>", table_cell_style), Paragraph("Bearer", table_cell_style), Paragraph("Atualiza todos os dados de um produto existente", table_cell_style), Paragraph("200 OK / 404", table_cell_style)],
        [Paragraph("<b>DELETE</b>", table_cell_style), Paragraph("<code>/api/products/{id}</code>", table_cell_style), Paragraph("Bearer", table_cell_style), Paragraph("Remove permanentemente o produto informado", table_cell_style), Paragraph("204 No Content", table_cell_style)],
        [Paragraph("<b>GET</b>", table_cell_style), Paragraph("<code>/docs.html</code>", table_cell_style), Paragraph("Pública", table_cell_style), Paragraph("Interface visual de documentação interativa da API", table_cell_style), Paragraph("200 OK", table_cell_style)],
    ]
    t_end = Table(endpoints_data, colWidths=[55, 125, 45, 204, 75])
    t_end.setStyle(TableStyle([
        ("BACKGROUND", (0, 0), (-1, 0), primary_color),
        ("GRID", (0, 0), (-1, -1), 0.5, colors.HexColor("#cbd5e1")),
        ("PADDING", (0, 0), (-1, -1), 4.5),
        ("ROWBACKGROUNDS", (0, 1), (-1, -1), [colors.white, bg_light]),
    ]))
    story.append(t_end)
    story.append(Spacer(1, 10))

    # Quebra de página para exemplos e frontend
    story.append(PageBreak())

    # ==================== SEÇÃO 6: EXEMPLOS DE REQUISIÇÃO E RESPOSTA ====================
    story.append(Paragraph("6. Exemplos de Payload e Respostas da API", h1_style))

    story.append(Paragraph("A. Autenticação e Login (<code>POST /api/auth/login</code>):", h2_style))
    sample_login = (
        "// Requisicao enviada:\n"
        "POST /api/auth/login HTTP/1.1\n"
        "Content-Type: application/json\n\n"
        "{\n"
        '  "username": "admin",\n'
        '  "password": "admin123"\n'
        "}\n\n"
        "// Resposta retornada (200 OK):\n"
        "{\n"
        '  "token": "YWRtaW46MTc5NDM2MDI2ODEzNA.oYD8rS2pZf6x7q...",\n'
        '  "username": "admin",\n'
        '  "role": "ADMIN"\n'
        "}"
    )
    t_login = Table([[Paragraph(f"<pre>{sample_login}</pre>", code_style)]], colWidths=[504])
    t_login.setStyle(TableStyle([("BACKGROUND", (0, 0), (-1, -1), colors.HexColor("#0f172a")), ("PADDING", (0, 0), (-1, -1), 6)]))
    story.append(t_login)
    story.append(Spacer(1, 8))

    story.append(Paragraph("B. Cadastro de Novo Produto (<code>POST /api/products</code>):", h2_style))
    sample_create = (
        "// Requisicao enviada:\n"
        "POST /api/products HTTP/1.1\n"
        "Authorization: Bearer YWRtaW46MTc5NDM2MDI2ODEzNA.oYD8rS2pZf6x7q...\n"
        "Content-Type: application/json; charset=utf-8\n\n"
        "{\n"
        '  "name": "Impressora Multifuncional Laser",\n'
        '  "description": "Impressao duplex automatica e conexao Wi-Fi",\n'
        '  "category": "Eletronicos",\n'
        '  "price": 1499.00,\n'
        '  "stock": 7\n'
        "}\n\n"
        "// Resposta retornada (201 Created):\n"
        "{\n"
        '  "id": 8,\n'
        '  "name": "Impressora Multifuncional Laser",\n'
        '  "description": "Impressao duplex automatica e conexao Wi-Fi",\n'
        '  "category": "Eletronicos",\n'
        '  "price": 1499.00,\n'
        '  "stock": 7,\n'
        '  "createdAt": "2026-11-09T22:24:27",\n'
        '  "updatedAt": "2026-11-09T22:24:27"\n'
        "}"
    )
    t_create = Table([[Paragraph(f"<pre>{sample_create}</pre>", code_style)]], colWidths=[504])
    t_create.setStyle(TableStyle([("BACKGROUND", (0, 0), (-1, -1), colors.HexColor("#0f172a")), ("PADDING", (0, 0), (-1, -1), 6)]))
    story.append(t_create)
    story.append(Spacer(1, 8))

    story.append(Paragraph("C. Resposta de Erro de Validação (<code>400 Bad Request</code>):", h2_style))
    sample_error = (
        "HTTP/1.1 400 Bad Request\n"
        "Content-Type: application/json; charset=UTF-8\n\n"
        "{\n"
        '  "timestamp": "2026-11-09T22:24:27.152",\n'
        '  "status": 400,\n'
        '  "error": "Dados Invalidos",\n'
        '  "message": "Erro de validacao nos campos informados.",\n'
        '  "errors": {\n'
        '    "name": "O nome do produto e obrigatorio.",\n'
        '    "price": "O preco nao pode ser negativo.",\n'
        '    "stock": "O estoque nao pode ser negativo."\n'
        "  }\n"
        "}"
    )
    t_error = Table([[Paragraph(f"<pre>{sample_error}</pre>", code_style)]], colWidths=[504])
    t_error.setStyle(TableStyle([("BACKGROUND", (0, 0), (-1, -1), colors.HexColor("#0f172a")), ("PADDING", (0, 0), (-1, -1), 6)]))
    story.append(t_error)
    story.append(Spacer(1, 10))

    # ==================== SEÇÃO 7: FRONTEND PURO ====================
    story.append(Paragraph("7. Arquitetura do Frontend Puro (Sem Frameworks)", h1_style))
    story.append(Paragraph(
        "A interface de usuário foi completamente reescrita em tecnologias web fundamentais (Vanilla), eliminando qualquer dependência de Node.js, npm, Webpack, Tailwind ou React:",
        body_style,
    ))
    story.append(Paragraph("• <b>HTML5 Semântico (<code>index.html</code>):</b> Estrutura leve, acessível e auto-suficiente com ícones vetoriais SVG inline embutidos diretamente no markup.", bullet_style))
    story.append(Paragraph("• <b>CSS3 Puro (<code>css/style.css</code>):</b> Utilização de variáveis CSS (Custom Properties) para temas de cores, tipografia fluida, Grid para cards de estatísticas, Flexbox para layouts e modais modernos com efeito de desfoque <code>backdrop-filter</code>.", bullet_style))
    story.append(Paragraph("• <b>Comunicação Assíncrona (<code>js/api.js</code>):</b> Módulo em JavaScript puro que consome a API REST através da <code>Fetch API</code> nativa, gerenciando a inclusão automática do token Bearer e interceptando respostas de erro 401 para redirecionamento ao login.", bullet_style))
    story.append(Paragraph("• <b>Lógica de DOM e Reatividade (<code>js/app.js</code>):</b> Atualização reativa da tabela, cálculo dinâmico dos 4 cards de métricas em tempo real, busca com <i>debounce</i> de digitação, modais de cadastro e edição, confirmação de exclusão e notificações <i>toast</i>.", bullet_style))
    story.append(Spacer(1, 10))

    # ==================== SEÇÃO 8: COMO EXECUTAR O SISTEMA ====================
    story.append(Paragraph("8. Instruções de Instalação e Execução", h1_style))
    story.append(Paragraph(
        "O sistema pode ser iniciado de duas maneiras flexíveis:",
        body_style,
    ))
    story.append(Paragraph(
        "<b>Modo 1 — Execução Local Rápida (sem Docker):</b><br/>"
        "1. Inicie o MySQL local (ex: via WAMP, XAMPP ou serviço nativo na porta 3306).<br/>"
        "2. No terminal, execute a aplicação com o Maven Wrapper integrado:<br/>"
        "&nbsp;&nbsp;&nbsp;&nbsp;<code>cd backend</code><br/>"
        "&nbsp;&nbsp;&nbsp;&nbsp;<code>.\\mvnw.cmd test package -DskipTests</code><br/>"
        "&nbsp;&nbsp;&nbsp;&nbsp;<code>java -jar target\\product-manager-1.0.0.jar</code><br/>"
        "3. Acesse o sistema no navegador em <b>http://localhost:8080/</b> com o usuário <b>admin</b> e senha <b>admin123</b>.",
        body_style,
    ))
    story.append(Paragraph(
        "<b>Modo 2 — Execução com Docker Compose:</b><br/>"
        "1. Na raiz do projeto, execute: <code>docker compose up --build</code><br/>"
        "2. O Docker orquestrará o MySQL (3306), a API Java Puro (8080) e o Frontend Nginx (3000).",
        body_style,
    ))
    story.append(Spacer(1, 10))

    # ==================== SEÇÃO 9: ROTEIRO DE APRESENTAÇÃO ====================
    story.append(Paragraph("9. Roteiro Sugerido de Apresentação Acadêmica (5 a 8 min)", h1_style))
    story.append(Paragraph("1. <b>Introdução (1 min):</b> Contextualizar o tema (Gestão de Produtos DMAXTECH) e frisar que todo o projeto foi feito em <b>Java Puro sem frameworks</b> e <b>Frontend Puro sem React/Tailwind</b>.", bullet_style))
    story.append(Paragraph("2. <b>Demonstração da Aplicação (2 min):</b> Acessar <code>http://localhost:8080/</code>, demonstrar login seguro com BCrypt, exibição dos cards de métricas em tempo real e busca instantânea.", bullet_style))
    story.append(Paragraph("3. <b>Demonstração do CRUD (2 min):</b> Cadastrar um produto novo, mostrar o recálculo dos indicadores, editar o preço ou estoque e realizar a exclusão confirmada por modal.", bullet_style))
    story.append(Paragraph("4. <b>Inspeção de Código em Camadas (2 min):</b> Exibir a separação entre <code>ProductController.java</code> (HttpHandler nativo), <code>ProductService.java</code> (regras de validação) e <code>ProductDAOImpl.java</code> (PreparedStatements JDBC puro).", bullet_style))
    story.append(Paragraph("5. <b>Conclusão (1 min):</b> Apresentar a documentação visual em <code>http://localhost:8080/docs.html</code> e comprovar a aprovação de 100% dos testes unitários.", bullet_style))

    # Compilação do documento
    doc.build(story, canvasmaker=NumberedCanvas)
    print(f">> PDF gerado com sucesso em: {output_filename}")


if __name__ == "__main__":
    out_file = os.path.abspath("DOCUMENTACAO_SISTEMA_E_API.pdf")
    create_pdf(out_file)

