package cn.jdfo.tool;

import com.fasterxml.jackson.annotation.JsonView;

import java.util.Scanner;


public class Pair<A,B> {
	@JsonView(View.SimpleRank.class)
	private A a;
	@JsonView(View.SimpleRank.class)
	private B b;

	public Pair() {
	}

	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}

	public A getA() {
		return a;
	}

	public void setA(A a) {
		this.a = a;
	}

	public B getB() {
		return b;
	}

	public void setB(B b) {
		this.b = b;
	}

	@Override
	public String toString() {
		return a+": "+b;
	}

    public static void main(String[] args){
        Scanner in = new Scanner(System.in);
        Node head=new Node(in.nextInt());
        Node handle=head;
        while(in.hasNextInt()){
            int n=in.nextInt();
            if(n!=0){
                head.next= new Node(n);
                head=head.next;
            }else break;
        }
        int k=in.nextInt();
        Node r=findNkthNode(handle,k);
    }

    private static class Node{
        int val;
        Node next;
        Node(){}
        Node(int v){val=v;}
    }

    private static Node findNkthNode(Node node,int k){
        int count=0;
        Node res=node;
        while(node!=null){
            count++;
            node=node.next;
        }
        int d=count/k;
        if(d>=count)return null;
        for(int i=0;i<d;i++){
            res=res.next;
        }
        return res;
    }
}
