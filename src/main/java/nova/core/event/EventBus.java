package nova.core.event;

/**
 * A general purpose event bus. This class is thread-safe and listeners can be
 * added or removed concurrently, no external locking is ever needed. Also, it's
 * very lightweight.
 *
 * @param <T> event type
 * @author Stan Hebben
 */
public class EventBus<T> {
	public static final int PRIORITY_HIGH = 100;
	public static final int PRIORITY_DEFAULT = 0;
	public static final int PRIORITY_LOW = -100;

	// TODO: actually test concurrency
	// TODO: unit tests

	// implements a linked list of nodes
	protected volatile EventListenerNode first = null;
	protected EventListenerNode last = null;

	public synchronized void clear() {
		first = last = null;
	}

	/**
	 * Adds an EventListener to the bus.
	 *
	 * @param listener event listener
	 * @return event listener's handle
	 */
	public EventListenerHandle<T> add(EventListener<T> listener) {
		return add(listener, PRIORITY_DEFAULT);
	}

	public EventListenerHandle<T> add(EventListener<T> listener, int priority) {
		EventListenerNode node = new EventListenerNode(listener, priority);

		synchronized (this) {
			if (first == null) {
				first = last = node;
			} else {
				// prioritized list: where to insert?
				EventListenerNode previousNode = last;
				while (previousNode != null && priority > previousNode.priority) {
					previousNode = previousNode.prev;
				}

				if (previousNode == null) {
					node.next = first;
					first.prev = previousNode;
					first = node;
				} else {
					if (previousNode.next == null) {
						last = node;
					} else {
						previousNode.next.prev = node;
					}

					previousNode.next = node;
					node.prev = previousNode;
				}
			}
		}

		return node;
	}

	/**
	 * Adds an EventListener to the list that only accepts a specific subclass
	 * of &lt;T&gt;
	 * 
	 * @param <E> -describe me-
	 * @param listener listener to register
	 * @param clazz class to listen for
	 * @return event listener's handle
	 */
	public <E extends T> EventListenerHandle<T> add(EventListener<E> listener, Class<E> clazz) {
		return add(new SingleEventListener<E, T>(listener, clazz), PRIORITY_DEFAULT);
	}

	public <E extends T> EventListenerHandle<T> add(EventListener<E> listener, Class<E> clazz, int priority) {
		return add(new SingleEventListener<E, T>(listener, clazz), priority);
	}

	/**
	 * Removes an EventListener from the list.
	 *
	 * @param listener listener to be removed
	 * @return true if the listener was removed, false it it wasn't there
	 */
	public synchronized boolean remove(EventListener<T> listener) {
		EventListenerNode current = first;

		while (current != null) {
			if (current.listener.equals(listener)) {
				current.close();
				return true;
			}

			current = current.next;
		}

		return false;
	}

	/**
	 * Checks if there are any listeners in this list.
	 *
	 * @return true if empty
	 */
	public boolean isEmpty() {
		return first == null;
	}

	/**
	 * Publishes an event by calling all of the registered listeners.
	 *
	 * @param event event to be published
	 */
	public void publish(T event) {
		EventListenerNode current;

		current = first;

		while (current != null) {
			current.listener.onEvent(event);

			synchronized (this) {
				current = current.next;
			}
		}
	}

	// #######################
	// ### Private classes ###
	// #######################

	protected class EventListenerNode implements EventListenerHandle<T> {
		protected final EventListener<T> listener;
		protected final int priority;
		protected EventListenerNode next = null;
		protected EventListenerNode prev = null;

		public EventListenerNode(EventListener<T> handler, int priority) {
			this.listener = handler;
			this.priority = priority;
		}

		@Override
		public EventListener<T> getListener() {
			return listener;
		}

		@Override
		public void close() {
			synchronized (EventBus.this) {
				if (prev == null) {
					first = next;
				} else {
					prev.next = next;
				}

				if (next == null) {
					last = prev;
				} else {
					next.prev = prev;
				}
			}
		}
	}

	/**
	 * A wrapper for an event listener that only accepts a specific type of
	 * event.
	 *
	 * @param <E> event type
	 * @param <T> super type
	 * @author Vic Nightfall
	 */
	protected static class SingleEventListener<E extends T, T> implements EventListener<T> {
		private final Class<E> eventClass;
		private final EventListener<E> wrappedListener;

		/**
		 * Constructs a new single typed Event listener.
		 *
		 * @param wrappedListener The listener which gets called when the event
		 *        was accepted.
		 * @param eventClass The event to listen for, Any posted event that is
		 *        an instance of said class will get passed through to the
		 *        wrapped listener instance.
		 */
		public SingleEventListener(EventListener<E> wrappedListener, Class<E> eventClass) {
			this.eventClass = eventClass;
			this.wrappedListener = wrappedListener;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void onEvent(T event) {
			if (eventClass.isInstance(event)) {
				wrappedListener.onEvent((E) event);
			}
		}
	}
}
