import React from 'react';
import './Messenger.css';

export enum MessageType {
  Error,
  Success,
}

export interface Message {
  type: MessageType
  text: string
  expireSeconds: number
}

export interface DatedMessage extends Message {
  posted: number
}

export interface MessengerConfig {
  messages: Array<DatedMessage>
  setMessages: React.Dispatch<React.SetStateAction<Array<DatedMessage>>>
}

export default class Messenger {
  config: MessengerConfig

  constructor(socketConfig: MessengerConfig) {
      this.config = socketConfig
  }

  flushMessages() {
    const now = Date.now()
    this.config.setMessages(this.config.messages.filter(msg => {
      const lifetimeSeconds = (now - msg.posted) / 1000
      const isExpired = lifetimeSeconds > msg.expireSeconds
      return !isExpired
    }));
  }
  
  pushMessage(msg: Message) {
    const datedMessage: DatedMessage = {
      ...msg,
      posted: Date.now()
    }
    this.config.setMessages([
      ...this.config.messages,
      datedMessage
    ]);
    setTimeout(() => {
      this.flushMessages();
    }, msg.expireSeconds * 1000 + 100)
  }

  render(messages: Array<Message>) {
    return (
      <div className="message-container">
        {messages.map((message, index) => {
          return (
            <div key={index} className={`message ${MessageType[message.type]?.toLowerCase() || ""}`}>{message.text}</div>
          );
        })}
      </div>
    )
  }
}

