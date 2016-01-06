package com.overtake.utils;

import java.util.*;

/**
 * 队列管理器
 * 
 * @author 畅彬
 * 
 */
public class QueueMan {
	/**
	 * 单例
	 */
	public static QueueMan _instance;
	/**
	 * 队列map
	 */
	private HashMap<String, Queue> _queues;

	public synchronized static QueueMan getInstance() {
		if (_instance == null) {
			_instance = new QueueMan();
		}

		return _instance;
	}

	public QueueMan() {
		_queues = new HashMap<String, Queue>();
	}

	/**
	 * 入队
	 * 
	 * @param item
	 * @param identity
	 * @param delegate
	 */
	public void pushItem(QueueItem item, String identity, QueueDelegate delegate) {
		Queue queue = _queues.get(identity);
		if (queue == null) {
			queue = new Queue(identity);
			queue.delegate = delegate;

			_queues.put(identity, queue);
		}

		queue.enqueue(item);

		if (!queue.isRunning) {
			next(identity);
		}
	}

	/**
	 * 处理下一个
	 * 
	 * @param identity
	 */
	public void next(String identity) {
		Queue queue = _queues.get(identity);
		if (queue != null) {
			queue.isRunning = false;
			queue.dequeue();
		}
	}

	/**
	 * 队列回调
	 * 
	 * @author 畅彬
	 * 
	 */
	public static interface QueueDelegate {
		public void queueRunOnItem(Queue q, QueueItem item);
	}

	/*
	 * 队列
	 */
	public static class Queue {

		/**
		 * 标识
		 */
		private String _identity;
		/**
		 * 队列元素
		 */
		private LinkedList<QueueItem> _items;
		/**
		 * 是否运行中
		 */
		public Boolean isRunning;

		/**
		 * 代理
		 */
		public QueueDelegate delegate;

		public Queue(String identity) {
			_identity = identity;
			_items = new LinkedList<QueueItem>();
			isRunning = false;
		}

		/**
		 * 入队
		 * 
		 * @param item
		 */
		public void enqueue(QueueItem item) {
			_items.addLast(item);
		}

		/**
		 * 出队
		 * 
		 * @return
		 */
		public QueueItem dequeue() {
			if (isRunning == true) {
				return null;
			}

			QueueItem item = null;
			if (!_items.isEmpty()) {
				item = _items.removeLast();
			}

			if (item != null) {
				if (delegate != null) {
					delegate.queueRunOnItem(this, item);
				}
			}

			return item;
		}

		public String identity() {
			return _identity;
		}

		public LinkedList<QueueItem> items() {
			return _items;
		}

	}

	/**
	 * 队列元素
	 * 
	 * @author 畅彬
	 * 
	 */
	public static class QueueItem {
		private Object _info;

		public QueueItem(Object info) {
			_info = info;
		}

		public Object info() {
			return _info;
		}

	}

}
