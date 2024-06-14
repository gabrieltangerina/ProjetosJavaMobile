package com.example.layoutideia.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.layoutideia.model.Cliente;
import com.example.layoutideia.model.Produto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CarrinhoViewModel extends ViewModel {

    // COLOCAR O CLIENTE AQUI TAMBÉM -  TENTAR COLOCAR ATRIBUTOS COMO ESTÁTICOS

    private static List<Produto> itensCarrinho = new ArrayList<>(); // É correto ser privado e estático?
    private static List<Produto> itensCarrinhoConsulta = new ArrayList<>();
    private static String formaPagamento = "Boleto";
    private static String operacaoVenda = "Venda";
    private static Cliente cliente;
    private static String descricao = "";

    public static void limpaItensCarrinhoConsulta(){
        itensCarrinhoConsulta.clear();
    }

    public static List<Produto> getItensCarrinhoConsulta() {
        return itensCarrinhoConsulta;
    }

    public static void setItensCarrinhoConsulta(List<Produto> itensCarrinhoConsulta) {
        CarrinhoViewModel.itensCarrinhoConsulta = itensCarrinhoConsulta;
    }

    public static void limparDadosViewModel(){
        itensCarrinho.clear();
        itensCarrinhoConsulta.clear();
        cliente = null;
        formaPagamento = "Boleto";
        operacaoVenda = "Venda";
        descricao = "";
    }

    public void adicionarItemCarrinho(Produto produto){
        itensCarrinho.add(produto);
    }

    public List<Produto> getItensCarrinho(){
        return this.itensCarrinho;
    }

    public void limparItensCarrinho(){
        this.itensCarrinho.clear();
    }

    public boolean buscaPorId(String id){
        for(Produto produto: itensCarrinho){
            if(produto.getCodigo().equals(id)){
                return true;
            }
        }
        return false;
    }

    public String calcularTotalPedido() {
        double total = 0.0;
        for (Produto produto : itensCarrinho) {
            total += produto.getPreco() * produto.getQuantidade();
        }

        return String.format("Total Pedido: R$%.2f", total);
    }

    public double getValorTotalPedido() {
        double total = 0.0;
        for (Produto produto : itensCarrinho) {
            total += produto.getPreco() * produto.getQuantidade();
        }

        return total;
    }

    public String removeItem(String codigo){
        try{
            Produto produto = this.buscaPorIdRetornaProduto(codigo);
            produto.setQuantidade(0);
            itensCarrinho.remove(produto);
            return produto.getNome() + " removido";
        }catch (Exception e){
            return "";
        }
    }

    public Produto buscaPorIdRetornaProduto(String id){
        for(Produto produto: itensCarrinho){
            if(produto.getCodigo().equals(id)){
                return produto;
            }
        }
        return null;
    }


    public void atualizarItemCarrinho(String id, String quantidade, String preco){
        for(Produto produto: itensCarrinho){
            if(produto.getCodigo().equals(id)){
                produto.setPreco(Double.parseDouble(preco));
                produto.setQuantidade(Integer.parseInt(quantidade));
                break;
            }
        }
    }

    public static String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public String getOperacaoVenda() {
        return operacaoVenda;
    }

    public void setOperacaoVenda(String operacaoVenda) {
        this.operacaoVenda = operacaoVenda;
    }
}