package adjancencylist

import grails.transaction.Transactional
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import demo.Comment
import demo.TreeService

@Transactional
@Slf4j
@CompileStatic
class CommentService implements TreeService {

    @Override
    @Transactional(readOnly = true)
    List<Comment> descendantsOfComment(Comment ancestorComment) {
        def comments = []
        for ( Comment comment : childsOfComment(ancestorComment)) {
            comments << comment
            comments += descendantsOfComment(comment)
        }
        comments
    }

    /**
     * Inmediate child
     */
    @Override
    @Transactional(readOnly = true)
    List<Comment> childsOfComment(Comment commentEntity) {
        CommentGormEntity.where {
            parent == commentEntity
        }.list()
    }

    @Override
    @Transactional(readOnly = true)
    List<Comment> ancestorsOfComment(Comment commentEntity) {
        def comments = []
        for ( Comment comment : parentsOfComment(commentEntity)) {
            comments << comment
            comments += ancestorsOfComment(comment)
        }
        comments
    }
    /**
     * Inmediate parents
     */
    @Transactional(readOnly = true)
    List<Comment> parentsOfComment(Comment commentEntity) {
        if ( !commentEntity || (commentEntity as CommentGormEntity).parent == null ) {
            return []
        }
        [(commentEntity as CommentGormEntity).parent]
    }

    @CompileDynamic
    void deleteComment(Comment comment) {
        def comments = [comment]
        comments += descendantsOfComment(comment)
        comments.each {
            (it as CommentGormEntity).delete()
        }
    }

    void moveCommentToParent(Comment comment, Comment newAncestor) {
        if ( !comment ) {
            return
        }
        CommentGormEntity entity = comment as CommentGormEntity
        entity.parent = newAncestor as CommentGormEntity
        if ( !entity.save() ) {
            log.error "Could not change parent of comment ${entity.errors}"
        }
        comment
    }

    Comment saveComment(String comment, String author, Comment ancestorComment) {
        def commentEntity = new CommentGormEntity(comment: comment, author: author, parent: ancestorComment as CommentGormEntity)
        if ( !commentEntity.save() ) {
            log.error "Could not save comment ${commentEntity.errors}"
        }
        commentEntity
    }

    @Transactional(readOnly = true)
    @Override
    Comment read(Long id) {
        CommentGormEntity.read(id)
    }
}
