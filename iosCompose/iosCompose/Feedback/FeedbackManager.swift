//
//  FeedbackManager.swift
//  iosApp
//
//  Created by Kevin Schildhorn on 4/30/19.
//  Copyright © 2019 Kevin Galligan. All rights reserved.
//

import UIKit
import shared

class FeedbackManager: NSObject,FeedbackApi {
    
    static let FeedbackDisabledNotificationName = "FeedbackDisabled"
    
    var viewController:UIViewController?
    private lazy var feedbackModel:FeedbackModel = FeedbackHelper().feedbackModel
    private lazy var notificationsModel :NotificationsModel = NotificationHelper().notificationModel
    
    func setViewController(_ vc: UIViewController){
        viewController = vc
    }
    
    func showFeedbackForPastSessions(){
        if(notificationsModel.feedbackEnabled) {
            feedbackModel.showFeedbackForPastSessions(listener: self)
        }
    }
    
    func close(){
        viewController?.dismiss(animated: true, completion: nil)
        viewController = nil
    }
    
    func disableFeedback(){
        notificationsModel.feedbackEnabled = false
        NotificationCenter.default.post(name: Notification.Name(FeedbackManager.FeedbackDisabledNotificationName), object: nil)
    }
    
    func generateFeedbackDialog(session: MyPastSession) {
        let test = viewController?.storyboard?.instantiateViewController(withIdentifier: "Feedback")
        let feedbackView = test as! FeedbackViewController
        feedbackView.providesPresentationContextTransitionStyle = true
        feedbackView.definesPresentationContext = true
        feedbackView.modalPresentationStyle = UIModalPresentationStyle.overCurrentContext
        feedbackView.modalTransitionStyle = UIModalTransitionStyle.crossDissolve
        viewController?.present(feedbackView, animated: true, completion: nil)
        feedbackView.setFeedbackManager(fbManager: self)
        feedbackView.setSessionInfo(sessionId: session.id, sessionTitle: session.title)
    }
    
    func finishedFeedback(sessionId:String, rating:Int, comment: String) {
        feedbackModel.finishedFeedback(sessionId: sessionId,rating: Int32(rating),comment: comment)
    }
    
    func onError(error: FeedbackApiFeedBackError) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

